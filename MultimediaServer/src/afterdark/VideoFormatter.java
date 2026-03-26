package afterdark;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

public class VideoFormatter {
	// where to look for videos
	private String pathToVideoFile;
	
	//the foramts and resolutions we want
	private static String[] formats = {"avi","mp4","mkv"};
	private static Map<String, Integer> resolutions = new LinkedHashMap<>();
    static {
        resolutions.put("240p", 240);
        resolutions.put("360p", 360);
        resolutions.put("480p", 480);
        resolutions.put("720p", 720);
        resolutions.put("1080p", 1080);
    }
	
	//categorize based on format and then resolution
	public Map<String,Map<String,List<String>>> publicVideoList = new HashMap<String, Map<String,List<String>>>();


	public VideoFormatter(String workpath) {
		pathToVideoFile = workpath;
		
		// initialize public-video-list
        for (String format : formats) {
            Map<String, List<String>> resolutionMap = new LinkedHashMap<>();
            for (String res : resolutions.keySet()) {
                resolutionMap.put(res, new ArrayList<String>()); // empty list for each resolution of each format
            }
            publicVideoList.put(format, resolutionMap);
        }
	}
	
	
	// Will look in the workdir given and 
	// 1) make with jaffree all the missing videos
	// 2) build the list of videos that will then be given to clients
	public void formatAllVideos() {
		File dir = new File(pathToVideoFile);
		
		// we will make sure this is a directory
		if(!dir.isDirectory()) {
			System.err.println("Not a directory : "+pathToVideoFile);
			return;
		}
		
		//then we will get all the files that are in the formats we support
		File[] videoFiles = dir.listFiles((file, name) -> (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mkv")));
		
		//and we will also make a set with the names for easy search
		Set<String> videoNames = new HashSet<>();
		if (videoFiles != null) {
			for (File f : videoFiles) videoNames.add(f.getName());
		}
		
		// Since we want to make the videos in all resolutions, we need to find for each video its highest resolution
		// We will find the highest video for example example_video-720p.mp4 and we will use it as the base for all formats 
		Map<String,List<String>> pairsHighestQuality = new HashMap<String,List<String>>();
		
		
		
		for(File file : videoFiles) {
			String[] parts = file.getName().split("[-.]");
			//Assume format where the only - is infornt of the quality and only one . exists
			if(parts.length!=3) {
				continue;
			}
			
			if(!pairsHighestQuality.containsKey(parts[0]) || isHigher(pairsHighestQuality.get(parts[0]).getFirst(), parts[1])) {
				pairsHighestQuality.put(parts[0], List.of(parts[1],parts[2]));
			}
		}
		
		//now for each video we have its highest qualiry.
		// So now we go over every format for every video and every resolution and we make whats missing
		for(String format : formats) {
			for(String videoName : pairsHighestQuality.keySet()) {
				String srcName = videoName+"-"+pairsHighestQuality.get(videoName).getFirst()+"."+pairsHighestQuality.get(videoName).getLast();
				Path videoSrc = Paths.get(pathToVideoFile+"/"+srcName);
				

				for(String resolutionKey : resolutions.keySet()) {
					String candidateName = videoName+"-"+resolutionKey+"."+format;
					
					if(videoNames.contains(candidateName)) {
						System.out.println("Already exists, skipping");
					} else {
						System.out.println("Making : "+candidateName);
						// we made jaffree do ffmpeg -i input.mp4 -vf scale=1280:720 output.mp4
						Path videoOut = Paths.get(pathToVideoFile+"/"+candidateName);
						int targetHeight = resolutions.get(resolutionKey);
						 
						try {
							FFmpeg.atPath()
							.addInput(UrlInput.fromPath(videoSrc))
							.addOutput(UrlOutput.toPath(videoOut)
									.addArguments("-vf", "scale=-2:" + targetHeight) // width=-2 keeps aspect ratio
									.setFormat(format.equalsIgnoreCase("mkv") ? "matroska" : format) // for some reason doing format .mkv wont work and needs the matroska key
							)
							.execute();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.err.println("Failed");
						}
					}
					
					/// here we also build video by video the stream list
					publicVideoList.get(format).get(resolutionKey).add(candidateName);
					
					//reached max quality
					if(resolutionKey.equalsIgnoreCase(pairsHighestQuality.get(videoName).getFirst())) {
						break;
					}
				}
			}
		}
		
		System.out.println(publicVideoList);	
	}
	
	private boolean isHigher(String current, String candidate) {
	    return resolutions.get(candidate) > resolutions.get(current);
	}
	
	//Adaptive quality
	// This will figure out the proper quality based on client speed
	public List<String> giveValidList(String format,String speed) {
		double speedKbps = Double.parseDouble(speed);
		
		String highestAdaptive = "";
		List<String> lists = new ArrayList<String>();

		if (speedKbps >= 4500) {          
		    highestAdaptive = "1080p";
		} else if (speedKbps >= 2500) {   
		    highestAdaptive = "720p";
		} else if (speedKbps >= 1000) {   
		    highestAdaptive = "480p";
		} else if (speedKbps >= 750) {    
		    highestAdaptive = "360p";
		} else {                          
		    highestAdaptive = "240p";
		}
		
		for(String resolution : publicVideoList.get(format).keySet()) {
			if(!isHigher(highestAdaptive, resolution) && !publicVideoList.get(format).get(resolution).isEmpty()) {
				lists.add(String.join(",", publicVideoList.get(format).get(resolution)));
			} else {
				break;
			}
		}
		System.out.println(lists);
		return lists;
	}
	
	//Streaming
	// here the process builder will do an ffmpeg coomand
	//https://trac.ffmpeg.org/wiki/StreamingGuide#Pointtopointstreaming
	public void streamVid(String vid,String proto,String port) {
		System.out.println("{SERVER} : will try to stream to port : "+port);
		try {
			ProcessBuilder process = new ProcessBuilder(
					"ffmpeg",
					"-re", //needed for streaming or else ffmpeg will move too fast https://trac.ffmpeg.org/wiki/StreamingGuide#The-reflag
					"-i",
					pathToVideoFile+"/"+vid,
					"-c:v", "libx264", "-c:a", "aac",
					"-f",
					proto.equalsIgnoreCase("rtp") ? "rtp" : "mpegts",
							proto+"://127.0.0.1:"+port
					);
			process.redirectErrorStream(true);
			Process pro = process.start();
			
			BufferedReader reader = new BufferedReader(
				    new InputStreamReader(pro.getInputStream())
				);

			new Thread(()->{
				String line;
				try {
					while ((line = reader.readLine()) != null) {
					    System.out.println("[FFMPEG] " + line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).run();
				
			int exit = pro.waitFor();
			System.out.println("Finished with "+ exit);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

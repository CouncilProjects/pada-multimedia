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
import java.util.logging.Logger;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;

public class VideoFormatter {
	// where to look for videos
	private String pathToVideoFile;
	Logger log = Logger.getLogger(VideoFormatter.class.getName());
	
	
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
		Initializer.addLogHandler(log);
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
			int dash = file.getName().lastIndexOf('-');
			int dot = file.getName().lastIndexOf('.');

			if(dash==-1 || dot==-1 || dash>dot) continue;

			String name = file.getName().substring(0, dash==-1?dot:dash);
			
			String extention = file.getName().substring(dot + 1);
			
			int height = FFprobe.atPath()
			        .setShowStreams(true)
			        .setInput(file.getAbsolutePath())
			        .execute()
			        .getStreams()
			        .stream()
			        .filter(s -> s.getHeight() > 0)
			        .mapToInt(s -> s.getHeight())
			        .findFirst()
			        .orElse(-1);

			if (height == -1) continue;
			if(height>1080) height=1080;

			String quality = height + "p";  // later we will be using semantics

			
			try {
				if(!pairsHighestQuality.containsKey(name) || isHigher(pairsHighestQuality.get(name).getFirst(), quality)) {
					pairsHighestQuality.put(name, List.of(quality,extention));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Bad video. Skipping");
			}
		}
		
		//now for each video we have its highest qualiry.
		// So now we go over every format for every video and every resolution and we make whats missing
		int existingVids=0;
		for(String format : formats) {
			for(String videoName : pairsHighestQuality.keySet()) {
				String srcName = videoName+"-"+pairsHighestQuality.get(videoName).getFirst()+"."+pairsHighestQuality.get(videoName).getLast();
				Path videoSrc = Paths.get(pathToVideoFile+"/"+srcName);
				

				for(String resolutionKey : resolutions.keySet()) {
					String candidateName = videoName+"-"+resolutionKey+"."+format;
					
					if(videoNames.contains(candidateName)) {
						existingVids++;
					} else {
						log.info("Making : "+candidateName);
						// we made jaffree do ffmpeg commands
						Path videoOut = Paths.get(pathToVideoFile+"/"+candidateName);
						int targetHeight = resolutions.get(resolutionKey);
						 
						try {
							FFmpeg.atPath()
							.setLogLevel(LogLevel.DEBUG)
							.addInput(UrlInput.fromPath(videoSrc))
							.addOutput(UrlOutput.toPath(videoOut)
									.addArguments("-vf", "scale=-2:" + targetHeight) // width=-2 keeps aspect ratio
									.addArguments("-c:v", "libx264")
									.addArguments("-c:a", "copy")
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
		log.info("[VIDEO HANDLER] Created missing videos. [pre-existing videos "+existingVids+"]");
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
		return lists;
	}
	
	//Streaming
	// here the process builder will do an ffmpeg coomand
	//https://trac.ffmpeg.org/wiki/StreamingGuide#Pointtopointstreaming
	public void streamVid(String vid,String proto,String port,String address) {
		VideoStats stats = new VideoStats();
		try {
			ProcessBuilder process = new ProcessBuilder(
					"ffmpeg",
					"-re", //needed for streaming or else ffmpeg will move too fast https://trac.ffmpeg.org/wiki/StreamingGuide#The-reflag
					"-i",
					pathToVideoFile+"/"+vid,
					"-progress","pipe:1", //https://ffmpeg.org/ffmpeg.html#toc-Main-options 
					"-c:v", "libx264", "-c:a", "aac",
					"-f",
					proto.equalsIgnoreCase("rtp") ? "rtp_mpegts" : "mpegts",
							proto+"://"+address+":"+port+"?listen"
					);
			process.redirectErrorStream(true);
			Process pro = process.start();
			
			BufferedReader reader = new BufferedReader(
				    new InputStreamReader(pro.getInputStream())
				);

			new Thread(new ProcessOutParser(reader, stats)).start();
				
			int exit = pro.waitFor();
			log.info(vid+" "+stats.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void cliDownload(String vid,String proto,String port,String address) {
		VideoStats stats = new VideoStats(); //will help when logging

		try {
			ProcessBuilder process = new ProcessBuilder(
					"ffmpeg",
					"-i",
					pathToVideoFile+"/"+vid,
					"-progress","pipe:1", //https://ffmpeg.org/ffmpeg.html#toc-Main-options 
					"-c:v", "copy",
					"-c:a", "copy", // we dont want to re-encode for download
					"-f",
					proto.equalsIgnoreCase("rtp") ? "rtp_mpegts" : "mpegts",
					proto+"://"+address+":"+port+"?listen"
				);
			process.redirectErrorStream(true);
			Process pro = process.start();
			
			BufferedReader reader = new BufferedReader(
				    new InputStreamReader(pro.getInputStream())
				);

			new Thread(new ProcessOutParser(reader, stats)).start();
				
			int exit = pro.waitFor();
			log.info(vid+" "+stats.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


class VideoStats{
	String fps=null;
	String bitrate=null;
	String playTime=null;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Time: " + replaceNull(playTime) +
                " | Bitrate: " + replaceNull(bitrate) +
                " | FPS: " + replaceNull(fps);
	}
	
	private String replaceNull(String in) {
		if(in!=null) {
			return in;
		} else {
			return "Unrelated";
		}
	}
}

class ProcessOutParser implements Runnable{
	// TODO Auto-generated method stub
    String line;
    BufferedReader reader;
    VideoStats stats;
    
	public ProcessOutParser(BufferedReader reader, VideoStats stats) {
		super();
		this.reader = reader;
		this.stats = stats;
	}
	
	@Override
	public void run() {
	    try {
	        while ((line = reader.readLine()) != null) {

	            if (!line.contains("=")) continue;

	            String[] parts = line.split("=", 2);
	            String key = parts[0];
	            String value = parts[1].trim();

	            switch (key) {
	                case "fps":
	                    
	                      stats.fps = value;
	                    
	                    break;

	                case "bitrate":
	                	stats.bitrate=value;
	                    break;

	                case "out_time":
	                    stats.playTime = value;
	                    break;

	                case "progress":
	                    if (value.equals("continue")) {
	                        // one full update received
	                        System.out.println(stats.toString());
	                    }
	                    break;
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
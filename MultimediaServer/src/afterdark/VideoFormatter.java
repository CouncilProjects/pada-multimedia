package afterdark;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FilterChain;
import com.github.kokorin.jaffree.ffmpeg.FilterGraph;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

public class VideoFormatter {
	private String pathToVideoFile;
	private static String[] formats = {"avi","mp4","mkv"};
	private static Map<String, Integer> resolutions = new LinkedHashMap<>();

    static {
        resolutions.put("240p", 240);
        resolutions.put("360p", 360);
        resolutions.put("480p", 480);
        resolutions.put("720p", 720);
        resolutions.put("1080p", 1080);
    }
	
	
	
	public VideoFormatter(String workpath) {
		pathToVideoFile = workpath;
	}
	
	
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
				Path videoSrc = Paths.get(pathToVideoFile+"/"+videoName+"-"+pairsHighestQuality.get(videoName).getFirst()+"."+pairsHighestQuality.get(videoName).getLast());
				

				for(String resolutionKey : resolutions.keySet()) {
					if(videoNames.contains(videoName+"-"+resolutionKey+"."+format)) {
						System.out.println("Already exists, skipping");
					} else {
						System.out.println("Making : "+videoName+"-"+resolutionKey+"."+format);
						// we made jaffree do ffmpeg -i input.mp4 -vf scale=1280:720 output.mp4
						Path videoOut = Paths.get(pathToVideoFile+"/"+videoName+"-"+resolutionKey+"."+format);
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
					
					//reached max quality
					if(resolutionKey.equalsIgnoreCase(pairsHighestQuality.get(videoName).getFirst())) {
						break;
					}
				}
			}
		}
		
	}
	
	private boolean isHigher(String current, String candidate) {
	    return resolutions.get(candidate) > resolutions.get(current);
	}
}

package afterdark;

public class Initializer {
	private Server centralServer;
	private VideoFormatter mediaFormatter;
	
	public void start() {
		System.out.println("Starting preparations");
		// Example: get videos path dynamically at runtime
        String videosPath = System.getProperty("user.home") + "/Videos/multimedia/videos";
        System.out.println("Will be using "+videosPath+" as the media folder");
        mediaFormatter = new VideoFormatter(videosPath);
		
		//first call videoformatter to format the videos directory
        mediaFormatter.formatAllVideos();
        
        centralServer = new Server(mediaFormatter);
		try {
			centralServer.startServer(5000);
		} catch (Exception e) {
			if(e.getMessage().equalsIgnoreCase("Server fail")) {
				System.out.println("No idea");
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		
		new Initializer().start();
	}

}

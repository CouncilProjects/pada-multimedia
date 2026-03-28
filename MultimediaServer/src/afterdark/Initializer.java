package afterdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Initializer {

	private VideoFormatter mediaFormatter;
	private LoadBalancer loadBalancer;
	private List<Server> servers = new ArrayList<Server>(); // reference to the servers
    // Default values
    String defaultDir = "/Videos/multimedia/videos";
    int defaultServers = 3;
	
	public void start() {
		// https://www.baeldung.com/jvm-shutdown-hooks#1-adding-hooks
		//We will be keeping a reference of the servers and we will take them down when the app is closed
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			for(Server s:servers) {
				s.stopServer();
			}
		}));
		
		Scanner scanner = new Scanner(System.in);

        // Ask for working directory
        System.out.print("Enter working directory [Default : " + defaultDir + "]: ");
        String dirInput = scanner.nextLine().trim();
        String workingDir = dirInput.isEmpty() ? defaultDir : dirInput;

        // Ask for number of servers
        System.out.print("Enter number of servers [Default : " + defaultServers + "]: ");
        String serverInput = scanner.nextLine().trim();
        int numServers;

        if (serverInput.isEmpty()) {
            numServers = defaultServers;
        } else {
            try {
                numServers = Integer.parseInt(serverInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default: " + defaultServers);
                numServers = defaultServers;
            }
        }
        scanner.close();
        
        
        
		System.out.println("Starting preparations");

        String videosPath = System.getProperty("user.home") + workingDir;
        System.out.println("Will be using "+videosPath+" as the media folder");
        
        //Create the videoHanlder and a LoadBalancer
        this.mediaFormatter = new VideoFormatter(videosPath);
        this.loadBalancer = new LoadBalancer();
        
        //Have the load balancer run
        new Thread(this.loadBalancer).start();
		
		//first call videoformatter to format the videos directory
        //The the videoFormatter will create all missing video files from the workDir
        mediaFormatter.formatAllVideos();
        
        // Try to spring up the number of requested servers. they will handle their load balancing registration themselves
		try {
			for(int i=0; servers.size()<numServers ;i++) {
				Server s = new Server(mediaFormatter, 5001+i, "ICE AD ID"+i); // Give a unique identifier for the Active Deployment instance just for logging purposes
				Thread t = new Thread(s);
				t.start();// if this fails the exception will be caught and a the server instance will not be counted
				servers.add(s);
			}
		} catch (Exception e) {
			if(e.getMessage().equalsIgnoreCase("Server fail")) {
				System.out.println("A server failed to be made");
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		new Initializer().start();
	}
}

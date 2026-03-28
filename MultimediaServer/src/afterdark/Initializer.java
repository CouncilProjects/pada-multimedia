package afterdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Initializer {
	private Server centralServer;
	private VideoFormatter mediaFormatter;
	private LoadBalancer loadBalancer;
	private List<Server> servers = new ArrayList<Server>(); // reference to the servers
	
	public void start() {
		// https://www.baeldung.com/jvm-shutdown-hooks#1-adding-hooks
		//We will be keeping a reference of the servers and we will take them down
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			for(Server s:servers) {
				s.stopServer();
			}
		}));
		
		Scanner scanner = new Scanner(System.in);

        // Default values
        String defaultDir = "/Videos/multimedia/videos";
        int defaultServers = 3;

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

        // Output result
        System.out.println("\nConfiguration:");
        System.out.println("Working Directory: " + workingDir);
        System.out.println("Number of Servers: " + numServers);

        scanner.close();
        
		System.out.println("Starting preparations");
		// Example: get videos path dynamically at runtime
        String videosPath = System.getProperty("user.home") + workingDir;
        System.out.println("Will be using "+videosPath+" as the media folder");
        
        this.mediaFormatter = new VideoFormatter(videosPath);
        this.loadBalancer = new LoadBalancer();
        new Thread(this.loadBalancer).start();
		
		//first call videoformatter to format the videos directory
        mediaFormatter.formatAllVideos();
        
        
		try {
			for(int i=0; i<numServers;i++) {
				Server s = new Server(mediaFormatter, 5001+i, "server ID"+i);
				Thread t = new Thread(s);
				servers.add(s);
				t.start();
			}
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

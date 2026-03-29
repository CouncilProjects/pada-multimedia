package afterdark;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;

import afterdark.ui.ServerGUI;
import afterdark.ui.UIListeners;

public class Initializer {
	Logger logger = Logger.getLogger(Initializer.class.getName());
	static SimpleFormatter formatter = new SimpleFormatter();
	
	static UIListeners ui;

	
	private VideoFormatter mediaFormatter;
	private LoadBalancer loadBalancer;
	private List<Server> servers = new ArrayList<Server>(); // reference to the servers
    // Default values
    String defaultDir = "/Videos/multimedia/videos";
    int defaultServers = 3;
	
	public void start(String workingDir,int numServers, UIListeners ui) {
		this.ui = ui;
		// https://www.baeldung.com/jvm-shutdown-hooks#1-adding-hooks
		//We will be keeping a reference of the servers and we will take them down when the app is closed
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			for(Server s:servers) {
				s.stopServer();
			}
		}));
		
		
        addLogHandler(logger);
        
        logger.info("Starting preparations");

        String videosPath = System.getProperty("user.home") + workingDir;
        logger.info("Will be using "+videosPath+" as the media folder");

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
				Server s = new Server(mediaFormatter, 5001+i, "ICE ID"+i); // Give a unique identifier for the Active Deployment instance just for logging purposes
				Thread t = new Thread(s);
				t.start();// if this fails the exception will be caught and a the server instance will not be counted
				servers.add(s);
			}
		} catch (Exception e) {
			if(e.getMessage().equalsIgnoreCase("Server fail")) {
				logger.info("A server failed to be made");
			}
		}
		
	}
	
	static Handler handle = new Handler() {
	    @Override
	    public void publish(LogRecord record) {
	        try {
				ui.log(formatter.format(record));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    @Override public void flush() {}
	    @Override public void close() {}
	};
	
	public static void addLogHandler(Logger log) {
		if(log.getHandlers().length>0) {
			return;
		}
		log.addHandler(handle);
	}
}

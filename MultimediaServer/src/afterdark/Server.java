package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;




public class Server implements Runnable {
	//Some static settings
	private static String loadBalancerIp="127.0.0.1";
	private static int loadBalancerPort=5000;
	
	
	private ServerSocket serverSocket;
	private VideoFormatter videoHandler;
	private int myPort=1;
	private String myId="default";
	
	private boolean running=true;
	
	

	
	public Server(VideoFormatter videoHandler,int port,String id) {
		super();
		this.videoHandler = videoHandler;
		this.myId=id;
		this.myPort = port;
	}
	
	
	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(myPort);
			
			//if OK inform the load balancer
			registerLoadBalancer();
			System.out.println("Server : "+myId+"done registering to load balancer");
			
			//if i registerd at the load balancer start 
			this.startServer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// DURING run the server will first register with the load balancer
	private void registerLoadBalancer() {
		try {
			Socket s= new Socket(loadBalancerIp,loadBalancerPort);
			PrintWriter out = new PrintWriter(s.getOutputStream(),true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			out.println("server|register|"+InetAddress.getLocalHost().getHostAddress()+":"+myPort);
			
			if(!in.readLine().equals("register-done")) {
				in.close();
				out.close();
				s.close();
				throw new Exception("load balancer did not respond");
			}
			in.close();
			out.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startServer() throws Exception {
		try {
			System.out.println("Server "+myId+" is active");
			while(running) {
				// the server will be accepting clients, and assigning them to threads
				new ClientHandler(serverSocket.accept(),videoHandler,myId).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Server fail");
		}
	}
	
	public void stopServer() {
		try {
			running = false;
			
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
}

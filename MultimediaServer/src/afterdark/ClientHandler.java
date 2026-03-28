package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


// notee this is a helpwer class that holds info for a video request that ffmpeg has not yet started. not for the ones that ffmpeg actevelty streams
class CurrentVidReqInfo{
	String video;
	String proto;
	String port;
	String action;
	
	void baseSetup(String video,String proto,String port,String act) {
		this.video=video;
		this.proto = proto;
		this.port = port;
		this.action = act;
	}
	
	
	List<String> getData() {
		List<String> info = new ArrayList<String>();
		
		info.add(video);
		info.add(proto);
		info.add(port);
		info.add(action);
		return info;
	}
}

public class ClientHandler extends Thread {
	private Socket clientSocket;
	private VideoFormatter videoHandle;
	private PrintWriter out;
    private BufferedReader in;
    private String serverId;
    private CurrentVidReqInfo reserved = new CurrentVidReqInfo();
    String command;
    String latestfreePort;
	
	public ClientHandler(Socket sock,VideoFormatter vidHandle,String serv) {
		clientSocket = sock;
		videoHandle = vidHandle;
		this.serverId = serv;
	}
	
	private void sendMessage(String command,String data) {
		out.println(command);
		out.println(data);
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			sendMessage("get-id", serverId);
		} catch (IOException e) {
			System.err.println("Failed to get streams");
			e.printStackTrace();
		}
		
		try {
			while((command = in.readLine()) != null) {
				System.out.println("[COMMAND] : "+command);
				if(command.equals("close")) {
					//0 means close
					handleClosing();
					return;
				} else if(command.equals("give-list")) {
					handleListgen();
				} else if(command.equals("video-req")) {
					handleVideoRequest();
				} else if(command.equals("cli-ready")) { //when the client is ready clear the pending request data and let ffmpgeg do its job
					List<String> info = reserved.getData();
					
					reserved.baseSetup(null, null,null,null);
					letFfmpeghandle(info.get(0), info.get(1),info.get(2),info.get(3));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	
	private void handleClosing() throws IOException {
		System.out.println("Was told to close [server]");
		sendMessage("close", "");
		clientSocket.close();
	}
	
	private void handleListgen() throws IOException {
		String[] incoming = in.readLine().split("\\|");
		String list = String.join(",",videoHandle.giveValidList(incoming[0], incoming[1]));
		sendMessage("get-list", list);
	}
	
	private void handleVideoRequest() throws IOException {
		String[] data = in.readLine().split("\\|");
		System.out.println(data[0]);
		
		//https://stackoverflow.com/questions/2675362/how-to-find-an-available-port
		//https://www.baeldung.com/java-free-port
			ServerSocket nextFreeSocket = new ServerSocket(0);
			int freeport = nextFreeSocket.getLocalPort();
			nextFreeSocket.close();
			latestfreePort = String.valueOf(freeport);
			reserved.baseSetup(data[1], data[0].toLowerCase(), latestfreePort,data[2]);
			
			sendMessage("get-port", latestfreePort); // the next step is most likely that the client will send a ready command
			
			
			
		
	}
	
	
	private void letFfmpeghandle(String vid,String proto,String port,String action) {
		System.out.println("{SERVER} will try to let ffmpeg hanlde "+vid+" in "+proto+" at "+port +" for "+action);
		if(action.equals("play")) {
			videoHandle.streamVid(vid,proto,port);
		} else {
			videoHandle.cliDownload(vid, proto, port);
		}
		
	}
}

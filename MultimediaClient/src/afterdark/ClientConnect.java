package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import afterdark.ui.IClientUi;

public class ClientConnect {
	private IClientUi uiLayer;
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private SpeedTestCallbacks callbacks;
    private String mySpeed="0";
    
    public ClientConnect(IClientUi cliUI) {
    	uiLayer = cliUI;
    	callbacks = new SpeedTestCallbacks();
    }
    
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        try {
			clientSocket = new Socket(ip, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			uiLayer.connectedOk();
			
			speedTest();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void speedTest() {
    	SpeedTester tester = new SpeedTester();
    	tester.setUp(
    			callbacks
    	);
    	tester.downlinkTest();
    }
    
    public class SpeedTestCallbacks {

    	
		public void completed(String Mbps) {
			mySpeed = Mbps;
    		uiLayer.speedTestDone();
    	}
		
		public void progress(String message,String octo,String bit) {
			uiLayer.setSpeedTestProgress(message, octo, bit);
		}
    }

    public String[] sendMessage(String command,String msg) throws IOException {
    	out.println(command);
        out.println(msg);
        String respCommand = in.readLine();
        String respData = in.readLine();
        return new String[] {respCommand,respData};
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    
    
    public void close() {
		try {
			String[] resp;
			if((resp=sendMessage("close", "I sent a close command Close"))[0].equalsIgnoreCase("close")) {
    			System.out.println("Was told to close [client]");
    			stopConnection();
    			
    			System.exit(0);
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    
    public void sendFormatSelection(String format) {
    	String[] respList;
    	String properFormat = format.replace(".", "");
    	System.out.println(properFormat);
    	try {
			if((respList = sendMessage("give-list", properFormat+"|"+mySpeed))[0].equals("get-list")) {
				String[] list = respList[1].split(",");
				System.out.println(list[0]);
				uiLayer.showVideoList(list);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

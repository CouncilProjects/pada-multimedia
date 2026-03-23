package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import afterdark.ui.IClientUi;

public class ClientConnect {
	private IClientUi uiLayer;
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    public ClientConnect(IClientUi cliUI) {
    	uiLayer = cliUI;
    	setListeners();
    }
    
    private void setListeners() {
		uiLayer.addButtonListener((e)->{
			testCon();
		});
		
		uiLayer.addCloseButtonListener((e)->{
			close();
		});
	}
    
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String command,String msg) throws IOException {
    	out.println(command);
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    
    
    public void testCon() {
    	String resp;
    	try {
    		
    		if((resp=sendMessage("test", "Test message from client")).equalsIgnoreCase("Done")) {
    			System.out.println("I was called to test");
    			uiLayer.onResponseRecieved(resp);
    			uiLayer.addButtonListener((e)->{});
    		}
    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    	}
		
	}
    
    public void close() {
		try {
			String resp;
			if((resp=sendMessage("close", "I sent a close command Close")).equalsIgnoreCase("close")) {
    			System.out.println("Was told to close [client]");
    			stopConnection();
    			uiLayer.signalClose();
    			System.exit(0);
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket clientSocket;
	private VideoFormatter videoHandle;
	private PrintWriter out;
    private BufferedReader in;
    String command;
	
	public ClientHandler(Socket sock,VideoFormatter vidHandle) {
		clientSocket = sock;
		videoHandle = vidHandle;
	}
	
	private void sendMessage(String command,String data) {
		out.println(command);
		out.println(data);
	}
	
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Failed to get streams");
			e.printStackTrace();
		}
		
		try {
			while((command = in.readLine()) != null) {
				System.out.print(command);
				if(command.equals("close")) {
					//0 means close
					System.out.println("Was told to close [server]");
					out.println("close");
					sendMessage("close", "");
					clientSocket.close();
					return;
				} else if(command.equals("give-list")) {
					String[] incoming = in.readLine().split("\\|");
					String list = String.join(",",videoHandle.giveValidList(incoming[0], incoming[1]));
					sendMessage("get-list", list);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("HELLOOOOO");
		}
	}
	
	
	
	private void test(String in) {
		System.out.println("Test complete");
	}
}

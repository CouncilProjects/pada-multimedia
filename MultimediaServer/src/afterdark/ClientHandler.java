package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket clientSocket;
	private PrintWriter out;
    private BufferedReader in;
    String command;
	
	public ClientHandler(Socket sock) {
		clientSocket = sock;
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
					clientSocket.close();
					return;
				} else if(command.equals("test")) {
					String firststStageInput = in.readLine();
					test(firststStageInput);
					out.println("Done");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void test(String in) {
		System.out.println("Test complete");
	}
}

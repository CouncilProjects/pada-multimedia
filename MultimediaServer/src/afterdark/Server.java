package afterdark;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	private ServerSocket serverSocket;
	private VideoFormatter videoHandler;
	
	

	
	public Server(VideoFormatter videoHandler) {
		super();
		this.videoHandler = videoHandler;
	}

	public void startServer(int port) throws Exception {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is active");
			while(true) {
				// the server will be accepting clients, and assigning them to threads
				new ClientHandler(serverSocket.accept(),videoHandler).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Server fail");
		}
	}
	
	public void stopServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

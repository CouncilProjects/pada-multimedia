package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



// the load balancer will always be oparating at port 5000
public class LoadBalancer implements Runnable{
	ServerSocket socket=null;
	List<String> availablePorts = new ArrayList<String>();
	private int roundRobinIndex = 0;
	
	
	public LoadBalancer() {
		try {
			this.socket = new ServerSocket(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startBalancing(){
		System.out.println("BALANCER ACTIVE");
		Socket sock = null;
		PrintWriter out = null;
		BufferedReader in = null;
		while(true) {
			try {
				sock = this.socket.accept();
				System.out.println("Connection");
				out = new PrintWriter(sock.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				String input = in.readLine();
				System.out.println(input);
				String[] request = input.split("\\|"); //Assume origin|command|data
				
				if(request[0].equals("server")) {
					if(request[1].equals("register")) { // i made contain a command part incase i need the LB to do extra stuff in the future
						availablePorts.add(request[2]);
						System.out.println("[LOAD BALANCER] registers port "+request[2]);
						sendMessage(out, "register-done", null);
					}
				} else if(request[0].equals("cli")) {
					System.out.println("[LOAD BALANCER] cli req "+request[1]);
					if(request[1].equals("give-server")) {
						sendMessage(out, "get-server",nextServerPort() );
					}	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	private void sendMessage(PrintWriter out,String command,String data) {
		out.println(command);
		out.println(data);
	}
	
	private String nextServerPort() {
        if (availablePorts == null || availablePorts.isEmpty()) {
            return null;
        }

        String value = availablePorts.get(roundRobinIndex);
        roundRobinIndex = (roundRobinIndex + 1) % availablePorts.size();
        return value;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.startBalancing();
	}
}
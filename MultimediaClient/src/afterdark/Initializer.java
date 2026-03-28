package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import afterdark.ui.IClientUi;
import afterdark.ui.MainWindow;

public class Initializer {
	//Some static settings
	private static String loadBalancerIp="127.0.0.1";
	private static int loadBalancerPort=5000;
	
	private static int serverPort; 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		//check if there is the systemdownload directory if not make it. 
		Path downloadPath = Paths.get(System.getProperty("user.home")+"/Ice-multimedia");
		if(!Files.exists(downloadPath)) {
			System.out.println("Making download directory");
			try {
				Files.createDirectory(downloadPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//then we first communicate with the load balancer
		try {
			Socket s= new Socket(loadBalancerIp,loadBalancerPort);

			PrintWriter out = new PrintWriter(s.getOutputStream(),true);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			out.println("cli|give-server|"+"");

			if(!in.readLine().equals("get-server")) {
				in.close();
				out.close();
				s.close();
				throw new Exception("load balancer did not respond");
			}
			
			serverPort = Integer.parseInt(in.readLine());
			
			System.out.println("I was added to "+serverPort);
			
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
		
		
 		
		SwingUtilities.invokeLater(() -> {
			IClientUi ui =  new MainWindow();
			ClientConnect controller = new ClientConnect(ui);
			((MainWindow) ui).setController(controller);
			ui.start(serverPort);
		});
	}

}

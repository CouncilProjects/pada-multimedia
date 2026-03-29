package afterdark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import afterdark.ui.IClientUi;
import afterdark.ui.dto.VideoAction;

public class ClientConnect {
	private IClientUi uiLayer;
	
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    private SpeedTestCallbacks callbacks;
    private String mySpeed="0";
    
    private String downPath = System.getProperty("user.home")+"/Ice-multimedia";
    
    private String serverIp;
    private int serverPort;
    
    public ClientConnect(IClientUi cliUI,String servIp,int servPort) {
    	this.uiLayer = cliUI;
    	this.callbacks = new SpeedTestCallbacks();
    	this.serverIp = servIp;
    	this.serverPort = servPort;
    }
    
    public void startConnection() throws UnknownHostException, IOException {
        try {
			clientSocket = new Socket(serverIp, serverPort);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			if(in.readLine().equals("get-id")) {
				uiLayer.connectedOk(in.readLine());
			}
			
			
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
    
    public void videoAction(VideoAction action) {
    	String[] respReq;
    	ProcessBuilder processBuild = null;
		try {
			if((respReq = sendMessage("video-req", action.getProto()+"|"+action.getVideo()+"|"+action.getAction()))[0].equals("get-stream-info")) {
				uiLayer.loadingVid("Loading "+action.getVideo());
				String[] respo = respReq[1].split("\\|");
				
				processBuild = createProcessBuild(action, respo); 
				
				
				processBuild.redirectErrorStream(true);
				//now we have the process start it
				Process process = processBuild.start();
				
				//also get its output for debugging
				BufferedReader reader = new BufferedReader(
					    new InputStreamReader(process.getInputStream())
					);
				
				//read the command output without blocking the thread
				new Thread(()->{
					String line;
					try {
						while ((line = reader.readLine()) != null) {
						    System.out.println("[FFMPEG] " + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}).start();
				
				//give prep time before leting server send the data
				Thread.sleep(500);
				
				//inform server
				out.println("cli-ready");

				new Thread(() -> {
				    try {
				        int exit = process.waitFor();

				        SwingUtilities.invokeLater(() -> {
				            uiLayer.doneLoading();
				        });

				    } catch (InterruptedException e) {
				        e.printStackTrace();
				    }
				}).start();
			}
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
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
    
    
    private ProcessBuilder createProcessBuild(VideoAction action,String[] streamInfo) {
    	if(action.getAction().equals("down")) {
			//About the command. 
    		// We want the windo to exit when the video gets downloaded
    		//(udp does not have a "stop" flag because its connectionless so we set a timeout so it stops after 3 sec of no data
			return new ProcessBuilder(
					"ffmpeg",
					"-timeout",
					"3000000",
					"-i",
					streamInfo[1]+"://"+serverIp+":"+streamInfo[0]+"?listen",
					downPath+"/"+action.getVideo(),
					"-y"
					
			);
    	} else if(action.getAction().equals("play")) {
    		//About the command. 
    		// We want the windo to exit when the video stops 
    		//(udp does not have a "stop" flag because its connectionless so we set a timeout so it stops after 3 sec of no data
    		//We also allow the user to use left and right buttons to go 3sec forward or backward using -seekinterval
    		//To also let the user know that the window that oppend is ours we set its title with window_title
    		return new ProcessBuilder(
					"ffplay",
					"-autoexit",  //https://ffmpeg.org/ffplay.html#toc-Advanced-options 
					"-rw_timeout",
					"3000000",
					"-seek_interval", //https://ffmpeg.org/ffplay.html#toc-While-playing
					"3000000",
					"-window_title", "ICE media streaming", //ffplay opens a new window but we can control the title
					"-i",
					streamInfo[1]+"://"+serverIp+":"+streamInfo[0]+"?listen"
			);
    	} else {
    		return null;
    	}
    }
}

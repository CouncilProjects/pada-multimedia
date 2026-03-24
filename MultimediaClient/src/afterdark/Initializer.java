package afterdark;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import afterdark.ui.IClientUi;
import afterdark.ui.MainWindow;

public class Initializer {
	private ClientConnect cliConnection;
	
	public void start(IClientUi ui) {
		//do a speed test
		cliConnection = new ClientConnect(ui);
		
		try {
			cliConnection.startConnection("127.0.0.1", 5000);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

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
		
		SwingUtilities.invokeLater(() -> {
			IClientUi ui =  new MainWindow();
			new Initializer().start(ui);
		});
	}

}

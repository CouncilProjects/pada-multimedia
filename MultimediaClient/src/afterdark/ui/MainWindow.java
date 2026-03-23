package afterdark.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Color;

public class MainWindow extends JFrame implements IClientUi{
	JButton testButton = new JButton("TestMe");
	JButton closeButton = new JButton("Close connection");
	
	
	public MainWindow() {
		setTitle("ICE media-player");
		setSize(300,400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		getContentPane().add(testButton);
		
		
		closeButton.setBackground(new Color(224, 27, 36));
		getContentPane().add(closeButton);
		
		setVisible(true);
	}

	@Override
	public void onResponseRecieved(String msg) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(() -> {
			testButton.setText(msg);
			testButton.setEnabled(false);
		});
	}

	@Override
	public void addButtonListener(ActionListener action) {
		testButton.addActionListener(action);
	}

	@Override
	public void signalClose() {
		// TODO Auto-generated method stub
		this.dispose();
	}

	@Override
	public void addCloseButtonListener(ActionListener action) {
		// TODO Auto-generated method stub
		closeButton.addActionListener(action);
		
	}
	
	
}

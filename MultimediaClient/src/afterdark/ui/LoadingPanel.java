package afterdark.ui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.border.EmptyBorder;

public class LoadingPanel extends JPanel {
	JLabel loadingLabel = new JLabel("Connecting to server...");
	JProgressBar progressBar = new JProgressBar();
	JLabel lblPleaseWait = new JLabel("Please wait");
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public LoadingPanel() {
		setBorder(new EmptyBorder(10, 30, 0, 30));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		
		loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(loadingLabel);
		
		
		progressBar.setBounds(new Rectangle(30, 30, 0, 12));
		progressBar.setForeground(new Color(248, 228, 92));
		progressBar.setIndeterminate(true);
		add(progressBar);
		
		
		lblPleaseWait.setBorder(new EmptyBorder(5, 0, 0, 0));
		lblPleaseWait.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblPleaseWait);

	}
	
	public void setLoadReason(String newtext) {
		loadingLabel.setText(newtext);
	}
	

}

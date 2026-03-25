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
import javax.swing.border.CompoundBorder;

public class LoadingPanel extends JPanel {
	JLabel loadingLabel = new JLabel("Connecting to server...");
	JProgressBar progressBar = new JProgressBar();
	JLabel lblPleaseWait = new JLabel("Please wait");
	private static final long serialVersionUID = 1L;
	private final JPanel speedTestInfoPanel = new JPanel();
	private final JLabel testInfoMessage = new JLabel("New label");
	private final JLabel testMBpsLabel = new JLabel("MBps : ");
	private final JLabel testMbpsLabel = new JLabel("Mbps : ");

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
		speedTestInfoPanel.setBackground(new Color(61, 56, 70));
		speedTestInfoPanel.setBorder(new CompoundBorder(new EmptyBorder(20, 0, 0, 0), new EmptyBorder(10, 10, 10, 10)));
		speedTestInfoPanel.setVisible(false);
		
		
		add(speedTestInfoPanel);
		
		speedTestInfoPanel.setLayout(new BoxLayout(speedTestInfoPanel, BoxLayout.Y_AXIS));
		testInfoMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedTestInfoPanel.add(testInfoMessage);
		testMBpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		speedTestInfoPanel.add(testMBpsLabel);
		testMbpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		speedTestInfoPanel.add(testMbpsLabel);
	}
	
	public void setLoadReason(String newtext) {
		loadingLabel.setText(newtext);
	}
	
	public void setTestResult(String mess,String octo,String mbps) {
		testInfoMessage.setText(mess);
		testMBpsLabel.setText("MBps : "+octo);
		testMbpsLabel.setText("Mbps : "+mbps);
		speedTestInfoPanel.setVisible(true);
	}
	

}

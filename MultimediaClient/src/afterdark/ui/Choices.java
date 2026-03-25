package afterdark.ui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.Box;
import javax.swing.JScrollPane;

public class Choices extends JPanel {
	JLabel explLabel = new JLabel("Select your prefered choices");
	JPanel panel = new JPanel();
	JComboBox formatComboBox = new JComboBox();
	JComboBox protocolComboBox = new JComboBox();
	JButton submitChoicesBtn = new JButton("Send selection");
	String[] videos = {};
	JSeparator separator = new JSeparator();
	ActionListener listener;
	
	
	private static final long serialVersionUID = 1L;
	private final JScrollPane videoSelectionScrollPane = new JScrollPane();
	private final JPanel videoSelectionPane = new JPanel();

	/**
	 * Create the panel.
	 */
	public Choices() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		explLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
		explLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(explLabel);
		
		
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, new Color(51, 209, 122)), new EmptyBorder(10, 10, 10, 10)));
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		fixComboHeight(formatComboBox);
		formatComboBox.setModel(new DefaultComboBoxModel(new String[] {".mp4", ".avi", ".mkv"}));
		formatComboBox.setBorder(new EmptyBorder(0, 30, 0, 30));
		formatComboBox.setMaximumRowCount(3);
		panel.add(formatComboBox);
		
		
		separator.setBorder(new EmptyBorder(3, 0, 3, 0));
		separator.setMaximumSize(new Dimension(32767, 3));
		panel.add(separator);
		
		
		submitChoicesBtn.setBackground(new Color(55, 168, 172));
		submitChoicesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitChoicesBtn.setBorder(UIManager.getBorder("Button.border"));
		panel.add(submitChoicesBtn);
		

		
		fixComboHeight(protocolComboBox);
		panel.add(protocolComboBox);
		protocolComboBox.setModel(new DefaultComboBoxModel(new String[] {"TCP","UDP",  "RDP"}));
		protocolComboBox.setBorder(new EmptyBorder(10, 30, 0, 30));
		protocolComboBox.setMaximumRowCount(3);
		protocolComboBox.setVisible(videos.length>0);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBorder(new EmptyBorder(3, 0, 3, 0));
		separator_1.setMaximumSize(new Dimension(32767, 3));
		panel.add(separator_1);
		
		panel.add(videoSelectionScrollPane);
		
		videoSelectionScrollPane.setViewportView(videoSelectionPane);
		videoSelectionPane.setLayout(new BoxLayout(videoSelectionPane, BoxLayout.Y_AXIS));
		submitChoicesBtn.addActionListener((e)->{userSubmitedFormat();});
	}
	
	private void fixComboHeight(JComboBox<?> combo) {
	    Dimension size = combo.getPreferredSize();
	    combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
	}
	
	public void getVideos(String[] vids) {
		videos = vids;
	}
	
	public void setUserAction(ActionListener action) {
		listener = action;
	}
	
	public void setVideos(String[] vids) {
		videos = vids;
		if(videos.length>0) {
			protocolComboBox.setVisible(true);
		}
		videoSelectionPane.removeAll();
		
		for(String vid : videos) {
			System.out.println(vid);
	        VideoControll lbl = new VideoControll(vid);
	        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
	        videoSelectionPane.add(lbl);
	        videoSelectionPane.add(Box.createVerticalStrut(10));
	        
	    }
		videoSelectionPane.revalidate();
		videoSelectionPane.repaint();
	}
	
	void userSubmitedFormat() {
		if(listener!=null) {
			listener.actionPerformed(
					new ActionEvent(this, ActionEvent.ACTION_PERFORMED, formatComboBox.getSelectedItem().toString())
			);
		}
	}

}

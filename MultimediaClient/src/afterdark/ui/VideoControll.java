package afterdark.ui;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

public class VideoControll extends JPanel {
	
	private static final long serialVersionUID = 1L;
	JLabel videonameLabel = new JLabel("video");
	JButton downloadBtn = new JButton("");
	JButton playButton = new JButton("");

	/**
	 * Create the panel.
	 */
	public VideoControll(String videoName) {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(94, 92, 100));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		String[] parts = videoName.split("[-.]");
		videonameLabel.setText(parts[0].replace("_", " ") + " "+ parts[1]);
		
		add(videonameLabel);
		downloadBtn.setBorderPainted(false);
		downloadBtn.setBackground(new Color(94, 92, 100));
		downloadBtn.setBorder(null);
		
		
		downloadBtn.setIcon(new ImageIcon(VideoControll.class.getResource("/assets/downloadIcon.png")));
		add(downloadBtn);
		playButton.setBorderPainted(false);
		playButton.setOpaque(true);
		playButton.setBackground(new Color(94, 92, 100));
		playButton.setBorder(null);
		
		
		playButton.setIcon(new ImageIcon(VideoControll.class.getResource("/assets/playIcon.png")));
		add(playButton);
		
	}

}

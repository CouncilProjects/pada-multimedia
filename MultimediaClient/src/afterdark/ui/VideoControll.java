package afterdark.ui;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import afterdark.ui.dto.VideoAction;

import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.awt.event.ActionEvent;




interface VideoActionListener{
	void onVideoInteract(VideoAction vidAction);
}




public class VideoControll extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private String video;
	JLabel videonameLabel = new JLabel("video");
	JButton downloadBtn = new JButton("");
	JButton playButton = new JButton("");
	
	VideoActionListener vidActionListener;
	
	
	
	public void setVidActionListener(VideoActionListener vidActionListener) {
		this.vidActionListener = vidActionListener;
	}

	public VideoControll(String videoName) {
		this.video = videoName;
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(94, 92, 100));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		String[] parts = videoName.split("[-.]");
		videonameLabel.setText(parts[0].replace("_", " ") + " "+ parts[1]);
		
		add(videonameLabel);
		downloadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(vidActionListener!=null) {
					vidActionListener.onVideoInteract(new VideoAction("down", videoName, null));
				}
			}
		});
		downloadBtn.setBorderPainted(false);
		downloadBtn.setBackground(new Color(94, 92, 100));
		downloadBtn.setBorder(null);
		
		
		downloadBtn.setIcon(new ImageIcon(VideoControll.class.getResource("/assets/downloadIcon.png")));
		add(downloadBtn);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(vidActionListener!=null) {
					vidActionListener.onVideoInteract(new VideoAction("play", videoName, null));
				}
			}
		});
		
		playButton.setBorderPainted(false);
		playButton.setOpaque(true);
		playButton.setBackground(new Color(94, 92, 100));
		playButton.setBorder(null);
		
		
		playButton.setIcon(new ImageIcon(VideoControll.class.getResource("/assets/playIcon.png")));
		add(playButton);
		
	}
	

}

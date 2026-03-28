package afterdark.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import afterdark.ClientConnect;

public class MainWindow extends JFrame implements IClientUi{
	ActionListener closeListener;
	private ClientConnect controller;
	JPanel footerPanel = new JPanel();
	JLabel lblPadaIce = new JLabel("2026-2027 PADA ICE ");
	
	JPanel contentPanel = new JPanel();
	CardLayout cardLayout = new CardLayout(0,0);
	LoadingPanel loaderView = new LoadingPanel();
	Choices choicespanel = new Choices();
	
	
	public void setController(ClientConnect controller) {
	    this.controller = controller;
	}
	
	public MainWindow() {
		setTitle("ICE media-player");
		setSize(400,600);
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(closeListener != null) {
					closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,"windowClosing"));
				}
				controller.close();
			}
		});
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		footerPanel.setBackground(new Color(55, 168, 172));
		setNewFooterheight();
		getContentPane().add(footerPanel, BorderLayout.SOUTH);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setNewFooterheight();
			}
		});
		
		footerPanel.add(lblPadaIce);
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(cardLayout);
		contentPanel.add(loaderView,"loading");
		contentPanel.add(choicespanel,"choice");
		
		choicespanel.setFormatChoice((e)->{
			controller.sendFormatSelection(e);
		});
		
//		this is a short version of doing 
//		new VidActionListener() {
//		    @Override
//		    public void onAction(SelectionData dtoIn) {
//		        Some code.
//		    }
//		}
		
		choicespanel.setVidActionListener(vidAction -> {
			controller.videoAction(vidAction);
		});
		
		setVisible(true);
	}
	
	private void setNewFooterheight() {
		int newheight = Math.max(getHeight() / 10, 40);
		footerPanel.setPreferredSize(new Dimension(0,newheight));
		footerPanel.revalidate();
		footerPanel.repaint();
	}
	
	@Override
	public void start() {
		try {
			controller.startConnection();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void signalClose() {
	    SwingUtilities.invokeLater(() -> {
	        this.dispose();
	    });
	}

	@Override
	public void connectedOk(String serverId) {
	    SwingUtilities.invokeLater(() -> {
	    	lblPadaIce.setText(lblPadaIce.getText()+" Server: "+serverId);
	        loaderView.setLoadReason("Doing a speed test");
	    });
	}

	@Override
	public void speedTestDone() {
	    SwingUtilities.invokeLater(() -> {
	        cardLayout.show(contentPanel, "choice");
	    });
	}

	@Override
	public void setSpeedTestProgress(String prog, String octo, String bit) {
	    SwingUtilities.invokeLater(() -> {
	        loaderView.setTestResult(prog, octo, bit);
	    });
	}

	@Override
	public void showVideoList(String[] list) {
	    SwingUtilities.invokeLater(() -> {
	        choicespanel.setVideos(list);
	    });
	}

	@Override
	public void loadingVid(String message) {
		System.out.println("DI IT");
	    SwingUtilities.invokeLater(() -> {
	        loaderView.setLoadReason(message);
	        cardLayout.show(contentPanel, "loading");
	        contentPanel.revalidate();
	        contentPanel.repaint();
	    });
	}

	@Override
	public void doneLoading() {
	    SwingUtilities.invokeLater(() -> {
	        cardLayout.show(contentPanel, "choice");
	    });
	}
	
	
	
	
}

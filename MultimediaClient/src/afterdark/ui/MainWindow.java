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
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame implements IClientUi{
	ActionListener closeListener;
	JPanel footerPanel = new JPanel();
	JLabel lblPadaIce = new JLabel("2026-2027 PADA ICE ");
	
	JPanel contentPanel = new JPanel();
	CardLayout cardLayout = new CardLayout(0,0);
	LoadingPanel loaderView = new LoadingPanel();
	Choices choicespanel = new Choices();
	
	
	
	public MainWindow() {
		setTitle("ICE media-player");
		setSize(300,400);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(closeListener != null) {
					closeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,"windowClosing"));
				}
				signalClose();
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
		
		setVisible(true);
	}
	
	private void setNewFooterheight() {
		int newheight = Math.max(getHeight() / 10, 40);
		footerPanel.setPreferredSize(new Dimension(0,newheight));
		footerPanel.revalidate();
		footerPanel.repaint();
	}

	@Override
	public void onResponseRecieved(String msg) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(() -> {

		});
	}

	@Override
	public void addButtonListener(ActionListener action) {

	}

	@Override
	public void signalClose() {
		this.dispose();
		
	}

	@Override
	public void addCloseButtonListener(ActionListener action) {
		closeListener = action;
		
	}

	@Override
	public void connectedOk() {
		// TODO Auto-generated method stub
		loaderView.setLoadReason("Doing a speed test");
	}
	
	@Override
	public void speedTestDone() {
		cardLayout.show(contentPanel, "choice");
		
	}
	
	
}

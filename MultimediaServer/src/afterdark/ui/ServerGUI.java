package afterdark.ui;

import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;

import afterdark.Initializer;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

public class ServerGUI extends JFrame implements UIListeners {

    private static final long serialVersionUID = 1L;
    
	private JTextField dirField;
    private JTextField serverField;
    private JButton startButton;
    private JPanel workDirPanel;
    private JPanel serversPanel;
    private JScrollPane scrollPane;
    private JTextArea logArea;
    
    public ServerGUI() {
        setTitle("ICE Streaming Server Initializer");
        setSize(500, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        startButton = new JButton("Start");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        workDirPanel = new JPanel();
        workDirPanel.setMaximumSize(new Dimension(32767, 30));
        workDirPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        getContentPane().add(workDirPanel);
                        workDirPanel.setLayout(new BorderLayout(0, 0));
                
                        // Components
                        JLabel dirLabel = new JLabel("Working Directory:");
                        workDirPanel.add(dirLabel, BorderLayout.WEST);
                dirField = new JTextField("/Videos/multimedia/videos");
                workDirPanel.add(dirField);
        
        serversPanel = new JPanel();
        serversPanel.setMaximumSize(new Dimension(32767, 30));
        serversPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        getContentPane().add(serversPanel);
                serversPanel.setLayout(new BoxLayout(serversPanel, BoxLayout.X_AXIS));
        
                JLabel serverLabel = new JLabel("Number of Servers:");
                serversPanel.add(serverLabel);
        serverField = new JTextField("3");
        serversPanel.add(serverField);
        getContentPane().add(startButton);
        
                JLabel lblLogging = new JLabel();
                lblLogging.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblLogging.setText("Logging");
                getContentPane().add(lblLogging);
        
                scrollPane = new JScrollPane();
                scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

                logArea = new JTextArea();
                logArea.setEditable(false);
                logArea.setLineWrap(true);
                logArea.setWrapStyleWord(true);

                

                // THIS IS THE FIX
                scrollPane.setViewportView(logArea);

                getContentPane().add(scrollPane);
        

        // Action
        startButton.addActionListener(e -> startSystem());
    }

    private void startSystem() {
        try {
            String dir = dirField.getText().trim();
            int servers = Integer.parseInt(serverField.getText().trim());

            if (dir.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Directory cannot be empty");
                return;
            }

            // Start backend
            new Thread(() -> {
                Initializer init = new Initializer();
                init.start(dir, servers,this);
            }).start();
            
            serverField.setEditable(false);
            dirField.setEditable(false);
            startButton.setText("Started");
            startButton.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Servers must be a valid number");
        }
    }

    public static void main(String[] args) {
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
            new ServerGUI().setVisible(true);
        });
    }
    
    public void log(String message) {
        
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
        });
    }
}

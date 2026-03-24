package afterdark.ui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Dimension;

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

public class Choices extends JPanel {
	JComboBox formatComboBox = new JComboBox();
	JComboBox protocolComboBox = new JComboBox();
	
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public Choices() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel explLabel = new JLabel("Select your prefered format and protocol");
		explLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
		explLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(explLabel);
		
		JPanel panel = new JPanel();
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
		
		JSeparator separator = new JSeparator();
		separator.setBorder(new EmptyBorder(3, 0, 3, 0));
		separator.setMaximumSize(new Dimension(32767, 3));
		panel.add(separator);
		

		
		fixComboHeight(protocolComboBox);
		protocolComboBox.setModel(new DefaultComboBoxModel(new String[] {"UDP", "TCP", "RDP"}));
		protocolComboBox.setBorder(new EmptyBorder(10, 30, 0, 30));
		protocolComboBox.setMaximumRowCount(3);
		panel.add(protocolComboBox);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBorder(new EmptyBorder(3, 0, 3, 0));
		separator_1.setMaximumSize(new Dimension(32767, 3));
		panel.add(separator_1);
		
		JButton submitChoicesBtn = new JButton("Send selection");
		submitChoicesBtn.setBackground(new Color(55, 168, 172));
		submitChoicesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitChoicesBtn.setBorder(UIManager.getBorder("Button.border"));
		panel.add(submitChoicesBtn);

	}
	
	private void fixComboHeight(JComboBox<?> combo) {
	    Dimension size = combo.getPreferredSize();
	    combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
	}

}

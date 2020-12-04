import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class InGameMessagePanel implements ActionListener {
	
	private JLabel messageLabel;
	private JButton okButton;
	private JLabel frameLabel;
	
	public InGameMessagePanel() {
		
		//get a panel
		ImageIcon panelFrame = new ImageIcon("/Assets/inGameMsgPanel_frame.png");
		frameLabel = new JLabel(panelFrame);
		//set size
        frameLabel.setBounds(700,600,400,200);
        frameLabel.setOpaque(false);
        
        //get text
        String messageText = "Default message text.";
        messageLabel = new JLabel(messageText);
        messageLabel.setFont(MainWindow.orbitron.deriveFont(15f));
        messageLabel.setForeground(Color.black);
        EmptyBorder border1 = new EmptyBorder(20, 0, 20,0 );
		messageLabel.setBorder(border1);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //get button
        okButton = new Buttons(" OK ");
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(this);
        
        //add text and button
        BoxLayout boxLayout = new BoxLayout(frameLabel, BoxLayout.PAGE_AXIS);
        frameLabel.setLayout(boxLayout);
        frameLabel.add(messageLabel);
        frameLabel.add(okButton);
        //hide panel
        frameLabel.setVisible(false);
	}
	
	public JLabel getFrameLabel() {
		return frameLabel;
	}

	public void setFrameLabel(JLabel frameLabel) {
		this.frameLabel = frameLabel;
	}

	public JLabel getMessageLabel() {
		return messageLabel;
	}

	public void setMessageLabel(JLabel messageLabel) {
		this.messageLabel = messageLabel;
	}

	public void SetMessageText(String message) {
		messageLabel.setText(message);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		//get the object that performed the action, respond accordingly		
		Object selected = event.getSource();
		
		//did user click "ok" on message panel?
		if(selected.equals(okButton)) {			
			//gameplay can continue
			GameSettings.GetGameController().BeginTurn();
		}
		
	}

}

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

public class Buttons extends JButton implements MouseListener {

	public Buttons(String text) {
		this.setVisible(true);
		this.setFont(MainWindow.orbitron);
		this.setForeground(Color.black);
		this.setContentAreaFilled(false);
		this.setText(text);
		this.setBorderPainted(false);
		this.addMouseListener(this);
		this.setFocusable(false);
		this.setBorderPainted(false);
	}

	public void setButtonBackground(Color color) {
		this.setBackground(color);
	}
	
	public void setButtonForeground(Color textColor) {
		this.setForeground(textColor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setForeground(new Color(95, 0, 14));
		GameSettings.playButtonSound();

	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setForeground(Color.black);
	}

}
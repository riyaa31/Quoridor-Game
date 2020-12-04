import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class InstructionsMenu extends JPanel implements ActionListener {

	// instructions
	JLabel instructionsHeaderLabel;
	JTextArea instructionsTextLabel;
	JButton instructionsBackButton;

	public InstructionsMenu() {

		// build a panel
		super();

		// header label at the top
		// instructions label in the center
		// button "Back" at the bottom

		// the instructions panel
		// size to fit frame
		this.setBounds(0, 0, 1200, 800);
		this.setOpaque(false);

		// the label for the header
		instructionsHeaderLabel = new JLabel("How To Play");
		EmptyBorder border1 = new EmptyBorder(20, 15, 0, 15);
		instructionsHeaderLabel.setBorder(border1);
		instructionsHeaderLabel.setAlignmentX(CENTER_ALIGNMENT);
		instructionsHeaderLabel.setFont(MainWindow.orbitron.deriveFont(72f));
		instructionsHeaderLabel.setForeground(Color.black);
		instructionsHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
		instructionsHeaderLabel.setVerticalAlignment(JLabel.CENTER);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		java.net.URL helpURL = getClass().getResource("/Assets/Instructions/instructions.html");
		if (helpURL != null) {
		    try {
		        editorPane.setPage(helpURL);
		    } catch (IOException e) {
		        System.err.println("Attempted to read a bad URL: " + helpURL);
		    }
		} else {
		    System.err.println("Couldn't find file: Instructions.html");
		}

		editorPane.setFont(MainWindow.orbitron.deriveFont(15f));
		//Put the editor pane in a scroll pane.
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250, 145));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));
		// instructions text
		JPanel instructionsPanel = new JPanel();
		EmptyBorder border2 = new EmptyBorder(50, 15, 0, 15);
		instructionsPanel.setOpaque(false);
		instructionsPanel.setBorder(border2);
		JScrollPane instructionsScrollPane = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		instructionsScrollPane.setOpaque(false);
		instructionsScrollPane.setPreferredSize(new Dimension(900, 500));
		editorPane.setPreferredSize(new Dimension(900, 600));
		editorPane.setOpaque(false);
		editorPane.setBackground(new Color(0, 0, 0, 0));
		instructionsScrollPane.setOpaque(false);
		instructionsPanel.add(instructionsScrollPane);
		instructionsPanel.setOpaque(false);
		
		/* Sets the text pane to transparent */
		instructionsScrollPane.getViewport().setOpaque(false);
		instructionsScrollPane.setBorder(BorderFactory.createEmptyBorder());
		instructionsScrollPane.setBackground(new Color(0, 0, 0, 0));
		// the "back" button and listener
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		instructionsBackButton = new Buttons("Back");
		instructionsBackButton.setForeground(Color.black);
		instructionsBackButton.setContentAreaFilled(false);
		instructionsBackButton.setBorderPainted(false);
		instructionsBackButton.setAlignmentX(CENTER_ALIGNMENT);
		instructionsBackButton.setFont(MainWindow.orbitron.deriveFont(30f));
		buttonPanel.add(instructionsBackButton);
		instructionsBackButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		instructionsBackButton.addActionListener(this);

		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(boxLayout);
		this.add(instructionsHeaderLabel);
		this.add(instructionsPanel);
		this.add(buttonPanel);
		this.setVisible(false);
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		// get the object that performed the action, respond accordingly
		Object selected = event.getSource();

		// was 'back' requested on instructions panel?
		if (selected.equals(instructionsBackButton)) {
			GameSettings.GetMainWindow().ShowPanel(GameSettings.GetMainMenu());
		}
	}

}

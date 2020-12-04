import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameBoard extends JPanel implements ActionListener {

	// note: this would eventually have to be changed to "extends JPanel", so that
	// it can be nested in a higher-level frame with menus, etc.
	// Frames cannot be nested!

	// panel to display the gridTile objs in a grid-style layout.
	JPanel gridPanel;

	// two-dim array, or table, to store the game tiles. [row][col].
	GameTile[][] grid;

	// size of grid. set in const'.
	int rows;
	int cols;

	/*
	 * NOTE: GameBoard needs new constructor to allow it to use an array of old
	 * tiles if loading a save game.
	 * 
	 */

	// constructor for NEW GAME
	public GameBoard(Color tileColor, Color wallColor, Color bkgColor, int rows, int cols) {
		// get rows, cols, and panel size
		this.rows = rows;
		this.cols = cols;

		// build a sub-panel to hold board
		JPanel gridPanel = new JPanel();
		//gridPanel.setMinimumSize(new Dimension(600, 600));
		//gridPanel.setPreferredSize(new Dimension(600, 600));
		this.add(gridPanel);
		GridLayout tileLayout = new GridLayout(rows, cols);
		tileLayout.setHgap(0);
		tileLayout.setVgap(0);
		gridPanel.setLayout(tileLayout);
		gridPanel.setBackground(bkgColor);
		this.setBackground(bkgColor);

		// build a grid of GameTiles inside the panel.
		grid = new GameTile[GameSettings.GetRows()][GameSettings.GetCols()];
		for (int x = 0; x < GameSettings.GetRows(); x++) {
			for (int y = 0; y < GameSettings.GetCols(); y++) {
				// spawn a gametile
				grid[x][y] = new GameTile(tileColor, wallColor, bkgColor, x, y);
				// add it to the panel
				gridPanel.add(grid[x][y]);
			}
		}

		// SET THIS VISIBLE OR NO??
		setVisible(true);
	}

	// Load constructor
	public GameBoard(GameTile[][] gameTiles, int rows, int cols) {
		// build a sub-panel to hold board
		JPanel gridPanel = new JPanel();

		this.add(gridPanel);
		GridLayout tileLayout = new GridLayout(rows, cols);
		tileLayout.setHgap(0);
		tileLayout.setVgap(0);
		gridPanel.setLayout(tileLayout);
		// build a grid of GameTiles inside the panel.
		grid = new GameTile[GameSettings.GetRows()][GameSettings.GetCols()];
		for (int x = 0; x < GameSettings.GetRows(); x++) {
			for (int y = 0; y < GameSettings.GetCols(); y++) {
				grid[x][y] = gameTiles[x][y];
				gridPanel.add(gameTiles[x][y]);
			}
		}
		// SET THIS VISIBLE OR NO??
		setVisible(true);
	}

	public void DisplayGameBoard() {
		setVisible(true);
	}

	public void HideGameBoard() {
		setVisible(false);
	}
	
	public void disableGameBoard() {
		setEnabled(false);
	}
	
	public void enableGameBoard() {
		setEnabled(true);
	}

	public GameTile[][] GetGrid() {
		return grid;
	}

	public GameTile GetGameTile(int rowAddress, int colAddress) {
		return grid[rowAddress][colAddress];
	}

	public int GetNumRows() {
		return rows;
	}

	public int GetNumCols() {
		return cols;
	}

	// Setters
	public void setGameGrid(GameTile[][] gameTiles) {
		grid = gameTiles.clone();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}

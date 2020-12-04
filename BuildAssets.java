import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/*
 * BuildAsset class.  This game is the factory for building the necessary game assets required to play.  
 * 
 * Responsible for taking input from either of TWO sources.
 * 
 * Option 1:  	Build a game from "new game" in main menu.  
 * 				Use new assets provided by menu to build a Game State.  
 * 
 * Option 2:  	Build a game from "load game" save file.
 * 				Use previous assets collected from save file to rebuild the previous GameState.
 * 
 */

public class BuildAssets {

	// size of grid.
	int rows;
	int cols;

	// the game board (collection of GameTiles).
	GameBoard gameBoard;

	// the players(4)
	Player[] players;

	// ref to the object that will handle user input
	InputManager inputManager;

	//
	// NEW GAME Constructor
	public BuildAssets(Color tileColor, Color wallColor, Color bkgColor, ArrayList<String> playerNames,
			ArrayList<String> playerTypes, Color[] playerColors, List<Integer> turnOrder) {

		// in order to generate a GameState we need
		// a board (has colors, size, quantity of tiles, etc, attached)
		// 4 players (have location, name, color, etc attached)
		// an indication of which is the next player. (player 1 for a new game).
		// since this is a new game, some assets will be given default values from
		// GameSettings

		// BOARD
		this.rows = GameSettings.GetRows();
		this.cols = GameSettings.GetCols();

		// Build a new game board (returns a JPanel with a grid of configured GameTiles
		// and extra methods)
		gameBoard = new GameBoard(tileColor, wallColor, bkgColor, rows, cols); // <----THE BOARD

		// give the game tiles to GameSettings for storage.
		// access these from anywhere with GameSettings.GetGameTiles(); returns a
		// GameTile[][]
		GameSettings.setGameTiles(gameBoard.GetGrid());

		GameSettings.setGameBoard(gameBoard);

		// give the game tiles to the InputManager for handling user input
		GameSettings.inputManager.SetGridTiles(gameBoard.GetGrid());

		// PLAYERS (4)
		// get an empty array that will hold 4 configured players, in proper turn order
		// (1, 2, 3, 4).
		players = new Player[4];

		// loop players. turn order is currently randomized (from GameSettings)
		// unrandomize it, store new players in proper order (starting with whomever was
		// chosen as first player)
		for (int i = 0; i < turnOrder.size(); i++) {

			// turn
			int playerTurn = turnOrder.get(i);
			// player location
			GameTile playerLocation = DetermineStartingPosition(playerTurn);
			// type and difficulty
			String playerType = playerTypes.get(i);
			Boolean isDifficult = false;
			if (playerType == "Human") {
				playerType = "h";
			} else if (playerType == "Computer(Easy)") {
				playerType = "c";
			} else if (playerType == "Computer(Hard)") {
				playerType = "c";
				isDifficult = true;
			} else {
				System.out.println("Player Type Not Identified!!");
			}
			// name
			String playerName = playerNames.get(i);
			// color
			Color playerColor = playerColors[i];

			// build a player
			Player newPlayer = new Player(playerTurn, playerLocation, playerType, playerName, playerColor, isDifficult);

			// add it to array in proper ordered position. -1 for zero indexing.
			players[newPlayer.GetTurnPosition() - 1] = newPlayer;
			// repeat until 4 players.

		}

		// NEXT PLAYER
		int nextPlayer = 0; // <--new game, so players[0] will be first to act;

		// add the gameboard to the middle panel of in-game UI.
		// middlePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

		// Store Player Objects in Game Settings
		// playersAttributes.addAll(Arrays.asList(players));
		GameSettings.setPlayers(players);

		// Build In-Game Menu UI (Panels surrounding the gameboard to display
		// information).
		InGameUIPanel inGameUIPanel = new InGameUIPanel();
		GameSettings.setInGameUIPanel(inGameUIPanel);
		
		// get ref to the middle panel, which will hold the game board.
		inGameUIPanel.setGameBoard(gameBoard);

		// set background of the settings panel to the appropiate bkg color
		inGameUIPanel.setSavePanelBG(bkgColor);

		// start the game controller
		new GameController(inGameUIPanel, gameBoard, players, nextPlayer, true);
	}

	/*
	 * Load Game Constructor
	 */
	public BuildAssets(Player[] players, GameTile[][] gameTiles, int nextTurn) {

		// Re create the game board from the save.
		this.rows = gameTiles[0].length;
		this.cols = gameTiles[1].length;

		GameBoard reloadedGameBoard = new GameBoard(gameTiles, rows, cols); // Initiate load game constructor

		// give the game tiles to GameSettings for storage.
		// access these from anywhere with GameSettings.GetGameTiles(); returns a
		// GameTile[][]
		GameSettings.setGameTiles(gameTiles);

		GameSettings.inputManager.SetGridTiles(gameTiles);

		// Store Player Objects in Game Settings
		GameSettings.setPlayers(players);

		// In game UI panel
		InGameUIPanel inGameUIPanel = new InGameUIPanel();
		inGameUIPanel.setGameBoard(reloadedGameBoard);
		GameSettings.setInGameUIPanel(inGameUIPanel);

		// set background of the settings panel to the appropiate bkg color
		inGameUIPanel.setSavePanelBG(gameTiles[0][0].getBkgColor());

		// Start the game controller
		new GameController(inGameUIPanel, reloadedGameBoard, players, nextTurn, false);
	}

	// helper method
	private GameTile DetermineStartingPosition(int turnOrder) {

		// player order determines starting position on board
		// 1 = bottom row, middle col
		// 2 = left column, middle row
		// 3 = top row, middle col
		// 4 = right column, middle row

		GameTile thisTile = null;
		int bottom = rows - 1;
		int left = 0;
		int top = 0;
		int right = cols - 1;
		int rowMiddle = (cols - 1) / 2;
		int colMiddle = (rows - 1) / 2;

		if (turnOrder == 1) {
			thisTile = gameBoard.GetGameTile(bottom, rowMiddle); // <---row, col
		} else if (turnOrder == 2) {
			thisTile = gameBoard.GetGameTile(colMiddle, left);
		} else if (turnOrder == 3) {
			thisTile = gameBoard.GetGameTile(top, rowMiddle);
		} else if (turnOrder == 4) {
			thisTile = gameBoard.GetGameTile(colMiddle, right);
		} else {
			System.out.println("Cannot assign turn order!!");
		}

		return thisTile;
	}

}

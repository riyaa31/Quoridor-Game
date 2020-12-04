import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameSettings implements Serializable {

	// store ref to the main UI window (a frame)
	// this frame will display a variety of panels depending on user needs
	static MainWindow mainWindow;
	static MainMenu mainMenu;
	static NewGameMenu newGameMenu;
	static LoadGameMenu loadGameMenu;
	static InstructionsMenu instructionsMenu;
	static QuitMenu quitMenu;
	static GameBoard gameBoard;
	static InGameUIPanel inGameUIPanel;

	// the in game message panel. has it's own methods for setting in-game text to display
	static InGameMessagePanel messagePanel;	

	// store reference to object which manages all incoming user input
	static InputManager inputManager;

	// store ref to the object which will run the gameplay
	static GameController gameController;


	// the number of rows for game board
	static int rows;
	// the number of cols for game board
	static int cols;
	// the size of the game tile
	static int tileWidth;
	static int tileHeight;

	// COLORS: Each color palette is designed to be accessible to anyones vision
	public static final Color tileColor1 = new Color(59, 64, 66);
	public static final Color wallColor1 = new Color(109, 61, 20);
	public static final Color bkgColor1 = new Color(0, 0, 0);
	public static final Color player1Color1 = new Color(255, 194, 10);
	public static final Color player2Color1 = new Color(12, 123, 220);
	public static final Color player3Color1 = new Color(26, 255, 26);
	public static final Color player4Color1 = new Color(95, 0, 14);

	public static final Color tileColor2 = new Color(97, 60, 31);
	public static final Color wallColor2 = new Color(237, 213, 183);
	public static final Color bkgColor2 = new Color(47, 47, 47);
	public static final Color player1Color2 = new Color(136, 54, 22);
	public static final Color player2Color2 = new Color(0, 108, 209);
	public static final Color player3Color2 = new Color(24, 254, 98);
	public static final Color player4Color2 = new Color(211, 95, 183);

	public static final Color tileColor3 = new Color(101, 78, 49);
	public static final Color wallColor3 = new Color(128, 0, 7);
	public static final Color bkgColor3 = new Color(0, 46, 78);
	public static final Color player1Color3 = new Color(230, 97, 0);
	public static final Color player2Color3 = new Color(93, 58, 155);
	public static final Color player3Color3 = new Color(26, 133, 255);
	public static final Color player4Color3 = new Color(212, 17, 89);
	
	public static final File buttonHoverSound = new File("/Assets/Sounds/button_sound.wav");

	// tile color options
	static Color[] tileColors = { tileColor1, tileColor2, tileColor3 };
	// wall color options
	static Color[] wallColors = { wallColor1, wallColor2, wallColor3 };
	// background color options
	static Color[] bkgColors = { bkgColor1, bkgColor2, bkgColor3 };

	// player colors (3 sets of four players each, in a 2D array)
	static Color[][] playerColors = { { player1Color1, player2Color1, player3Color1, player4Color1 },
			{ player1Color2, player2Color2, player3Color2, player4Color2 },
			{ player1Color3, player2Color3, player3Color3, player4Color3 } };
	
	//set upon selection by user
	static Color activeWallColor;
	
	// store collection of names that AI can use
	static String[] computerNames;

	// a collection of 1, 2, 3, 4 in a random order. used to assign ID's.
	static List<Integer> playerIDList;

	// storage for the individual tiles needed to comprise a gameboard.
	// passed here by BuildAssets upon creation.
	static GameTile[][] gameTiles;
	
	//arraylist of 9 specific goal tiles for the current player.
	static ArrayList<GameTile> goalTiles;

	// Storage for the players Objects
	static Player[] players;

	// for locking controls/input when necessary (ie: between turn changes)
	static Boolean gameIsPaused;
	
	//flag for game over
	static Boolean gameIsOver;

	/*
	 * Constructor - GameSettings
	 * 
	 * Intended as a top-level class to provide access for static variables. Any
	 * class can access these attributes like this GameSettings.GetInputManager();
	 * GameSettings.GetPlayerColors(index);
	 */
	public GameSettings() {

		// set tile size (includes 1/2 wall width on each side)
		// recommend 90x90 if using a 1000x1000 frame.
		tileWidth = 60;
		tileHeight = 60;

		// set grid size for play
		rows = 9;
		cols = 9;

		// create names
		computerNames = new String[] { "Robyn", "Yousuf", "Shannon", "Dave", "Lemar", "Oscar", "Winnie",
										"Frank", "Jamal", "Karina", "Rollo", "James", "Olaf", "Brian"};

		// create a random list for assigning player ID's
		playerIDList = new ArrayList<>();
		playerIDList = buildPlayerIDList();

		// build and store an input manager to interface with user (mouse move/click)
		inputManager = new InputManager();

		// create and store a MainWindow frame to begin gameplay (starts at main menu).
		mainWindow = new MainWindow();

		// Player Object storing
		players = new Player[4];

		// game is not paused
		gameIsPaused = false;
	}
	
	/*
	 * Playsounds on hover
	 */
	public static void playButtonSound()
	{
	    try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(GameSettings.class.getResource("/Assets/Sounds/button_sound.wav")));
	        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        gainControl.setValue(-30.0f);
	        clip.start();
	    }
	    catch (Exception exc)
	    {
	    }
	}

	// getters
	public static GameController GetGameController() {
		return gameController;
	}

	public static InputManager GetInputManager() {
		return inputManager;
	}

	public static MainWindow GetMainWindow() {
		return mainWindow;
	}

	public static MainMenu GetMainMenu() {
		return mainMenu;
	}

	public static NewGameMenu GetNewGameMenu() {
		return newGameMenu;
	}

	public static LoadGameMenu GetLoadGameMenu() {
		return loadGameMenu;
	}

	public static InstructionsMenu GetInstructionsMenu() {
		return instructionsMenu;
	}
	
	public static GameBoard getGameBoard() {
		return gameBoard;
	}

	public static QuitMenu GetQuitMenu() {
		return quitMenu;
	}
	
	
	public static InGameUIPanel getInGameUIPanel() {
		return inGameUIPanel;
	}

	public static void setInGameUIPanel(InGameUIPanel inGameUIPanel) {
		GameSettings.inGameUIPanel = inGameUIPanel;
	}

	// public static InGameMessagePanel GetMessagePanel() {
	// return messagePanel;
	// }

	public static int GetRows() {
		return rows;
	}

	public static int GetCols() {
		return cols;
	}

	public static int getTileWidth() {
		return tileWidth;
	}

	public static int getTileHeight() {
		return tileHeight;
	}

	public static GameTile[][] getGameTiles() {
		return gameTiles;
	}
	
	public static ArrayList<GameTile> getGoalTiles() {
		return goalTiles;
	}

	// get computer-generated name
	public static String GetRandomName() {
		int rnd = new Random().nextInt(computerNames.length);
		return computerNames[rnd];
	}

	// get player ID List (randomized 1-4)
	public static List<Integer> getPlayerIDList() {
		return playerIDList;
	}

	// returns a single color
	// use index 0, 1, or 2
	public static Color getTileColor(int tileIndex) {
		return tileColors[tileIndex];
	}

	// returns a single color
	// use index 0, 1, or 2
	public static Color getWallColor(int wallIndex) {
		return wallColors[wallIndex];
	}
	
	public static Color getActiveWallColor() {
		return activeWallColor;
	}

	// returns a single color
	// use index 0, 1, or 2
	public static Color getBkgColor(int bkgIndex) {
		return bkgColors[bkgIndex];
	}

	// this returns an array of 4 colors
	// use index 0, 1, or 2
	public static Color[] getPlayerColors(int playersIndex) {
		return playerColors[playersIndex];
	}

	public static Player[] getPlayers() {
		return players;
	}

	public static Boolean GetGameIsPaused() {
		return gameIsPaused;
	}
	
	public static Boolean GetGameIsOver() {
		return gameIsOver;
	}	
	
	public static InGameMessagePanel GetMessagePanel() {
		return messagePanel;
	}
	
	
	
	
	// SETTERS

	public static void SetMessagePanel(InGameMessagePanel messagePanel) {
		GameSettings.messagePanel = messagePanel;
	}	

	public static void SetMainWindow(MainWindow mainWin) {
		GameSettings.mainWindow = mainWin;
	}

	public static void SetMainMenu(MainMenu mainMenu) {
		GameSettings.mainMenu = mainMenu;
	}

	public static void SetNewGameMenu(NewGameMenu newGameMenu) {
		GameSettings.newGameMenu = newGameMenu;
	}

	public static void SetLoadGameMenu(LoadGameMenu loadGameMenu) {
		GameSettings.loadGameMenu = loadGameMenu;
	}

	public static void SetInstructionsMenu(InstructionsMenu instructionsMenu) {
		GameSettings.instructionsMenu = instructionsMenu;
	}

	public static void SetQuitMenu(QuitMenu quitMenu) {
		GameSettings.quitMenu = quitMenu;
	}

	// public static void SetMessagePanel(InGameMessagePanel msgPanel) {
	// GameSettings.messagePanel = msgPanel;
	// }
	
	public static void setGameBoard(GameBoard gameBoard) {
		GameSettings.gameBoard = gameBoard;
	}

	public static void setGameTiles(GameTile[][] allTiles) {
		// this is only set by two methods:
		// BuildAssets, in constructor, when building a new game board
		// LoadGame, in LoadFromFile(), when loading previously saved files.
		gameTiles = allTiles;
	}
	
	public static void setGoalTiles(ArrayList<GameTile> currentGoalTiles) {
		goalTiles = currentGoalTiles;
	}
	
	public static void setActiveWallColor(Color selectedColor) {
		activeWallColor = selectedColor;
	}

	public static void setPlayers(Player[] players2) {
		players = players2;
	}

	public static void SetGameIsPaused(Boolean isPaused) {
		GameSettings.gameIsPaused = isPaused;
	}
	
	public static void SetGameIsOver(Boolean isFinished) {
		GameSettings.gameIsOver = isFinished;
	}

	public static void SetGameController(GameController controller) {
		GameSettings.gameController = controller;
	}

	// helper methods
	private List<Integer> buildPlayerIDList() {
		// randomize the numbers 1-4 inside an array. use collections->shuffle().
		// ex: [1, 3, 4, 2] or [2, 1, 4, 3] etc...

		List<Integer> sourceList = Arrays.asList(1, 2, 3, 4);
		Collections.shuffle(sourceList);
		// return the shuffled source list.
		return sourceList;
	}
}

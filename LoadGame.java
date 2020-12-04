import java.awt.Color;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/*
 * 	LoadGame reads a stream of objects to re-build a Saved game.
 */
public class LoadGame {

	private static FileInputStream saveFile;

	private LoadGame() {
	}

	/*
	 * Loads back previously saved objects for resuming a Saved Game
	 * 
	 * @param: String filename, the save file requested.
	 */
	public static void loadGameObjs(String filename) {
		// Create the data objects for us to restore.
		Player[] players = new Player[4];
		GameTile[][] gameTiles = new GameTile[9][9];

		try {
			// Open file to read Save.sav
			if (filename.equals("Save1.sav")) {
				saveFile = new FileInputStream("Save1.sav");
			}
			if (filename.equals("Save2.sav")) {
				saveFile = new FileInputStream("Save2.sav");
			}
			if (filename.equals("Save3.sav")) {
				saveFile = new FileInputStream("Save3.sav");
			}

			// Create an ObjectInputStream to get objects from save file.
			ObjectInputStream save = new ObjectInputStream(saveFile);

			players = (Player[]) save.readObject(); // Restore Player Objects
			gameTiles = (GameTile[][]) save.readObject(); // Load Game tiles
			int nextTurn = (int) save.readObject(); // Restore next turn
			
			// Create BuildAssets Load Game constructor.
			loadGameBuild(players, gameTiles, nextTurn);

			save.close(); // Close Object Input.
			saveFile.close(); // Close File input.
		} catch (Exception FileNotFoundException) {
			GameSettings.GetLoadGameMenu().setSelectionLabel("No Save File Found: " + filename, Color.red);
		}

	}

	/*
	 * Build new LoadGame.
	 * 
	 * @params: Player[] players (the saved player objs), GameTile[][] gameTiles
	 * (saved game obj), int nextTurn (who was next move)
	 */
	private static void loadGameBuild(Player[] players, GameTile[][] gameTiles, int nextTurn) {

		new BuildAssets(players, gameTiles, nextTurn); // Load Game BuildAssets constructor
	}

}

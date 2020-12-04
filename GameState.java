

/*
 * The GameState holds a list of assets and attributes that define a game in progress (possibly just starting). 
 */
public class GameState {
	
	// list of player objects. Must contain 4 Players.
	Player[] players;
	// the tile set that the game is using
	GameTile[][] gameTiles;	
	// the player who has the next turn (playerID 1, if a new game)
	int currentPlayerIndex;

	/*
	 * Constructor - GameState 
	 * A snapshot of a game in progress.  Like an autosave.
	 * Should be implemented at the beginning of each turn.
	 * Could be used for networking between clients and a server.
	 * Could also be used for undo, or replay entire game from any point.
	 * 
	 * @Params: players = a collection of 4 pre-built players gameTiles = human or
	 * computer? accepts "h" or "c". isDifficult = difficulty level for AI play.
	 * true = hard/challenging. false = easy.
	 */
	public GameState(Player[] currentPlayers, GameTile[][] currentTiles, int currentPlayerIndex) {

		this.players = currentPlayers;
		this.gameTiles = currentTiles;
		//could use a nextPlayerIndex instead, if saving GameState at the end of a turn.
		this.currentPlayerIndex = currentPlayerIndex; 
		
		//these items can be used to rebuild any game
		//they can easily be passed through the BuildAssets constructor.
		
		//could save these to GameSettings (arrayList?) for reload capability
		//GameSettings.SaveGameState(this);   
		
		//could also send this GameState to a server for network play.
		
	}
	
	public Player[] getPlayers() {
		return players;
	}

	public GameTile[][] getGameTiles() {
		return gameTiles;
	}	
	
	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}	
}

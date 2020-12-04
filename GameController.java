import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameController {

	// cache ref to players and turn info
	private static Player[] allPlayers;
	// index for keeping track of turns for players (0-3)
	private static int nextPlayerIndex;
	private static Player currentPlayer;
	
	//when a player takes a turn, these tiles are illuminated as the goal.
	private static ArrayList<GameTile> goalTiles;
	private static ArrayList<JPanel> goalPanels;	

	// store reference to collection of tiles
	GameTile[][] tiles;

	// the GameState which will store a snapshot of a game in progress
	GameState gameState;

	// constructor
	public GameController(InGameUIPanel inGameUIPanel, GameBoard gameBoard, Player[] players, int nextToPlay,
			boolean isNewGame) {

		// copy ref to GameSettings
		GameSettings.SetGameController(this);
		GameController.allPlayers = players;
		// For a new game, nextPlayer = 0. Then 1, 2, 3, 0, 1, 2, 3...
		GameController.nextPlayerIndex = nextToPlay;
		GameController.currentPlayer = allPlayers[nextPlayerIndex];
		setNextPlayer(nextToPlay);
		
		//configure for displaying goal tiles. jlayeredpanes
		goalTiles = new ArrayList<GameTile>();
		GameSettings.setGoalTiles(goalTiles);
		goalPanels = new ArrayList<JPanel>();
		for(int i = 0; i < GameSettings.GetRows(); i++) {
			JPanel thisPanel = new JPanel();			
			thisPanel.setBounds(0, 0, 60, 60);
			//thisPanel.setVisible(true);
			//thisPanel.setOpaque(false);
			
			ImageIcon starIcon = new ImageIcon(getClass().getResource("/Assets/goldStar.png"));
			JLabel starLabel = new JLabel(starIcon);			
			thisPanel.setLayout(new GridBagLayout());  //no constraints, so label is centered h/v.
			thisPanel.add(starLabel);			
			goalPanels.add(thisPanel);
		}

		// cache the tiles
		this.tiles = gameBoard.GetGrid();
		//if this is a new game, place the players on the tiles.
		//otherwise players are already located on a tile.
		if (isNewGame) {
			// put the players in position on the board
			for (Player thisPlayer : players) {
				// get the assigned starting tile
				GameTile startTile = thisPlayer.GetTile();
				// put player on board by adding it to the tile for display
				startTile.AddPlayer(thisPlayer);
			}
		}

		// show the gameboard!
		GameSettings.GetMainWindow().ShowPanel(inGameUIPanel.getMainPanel());
		
		// advance to next turn, pass player list index as ref.
		AdvanceToNextTurn();	
	}

	// GETTERS and setters.  
	public static Player GetCurrentPlayer() {
		// return the player taking the current turn
		return currentPlayer;
	}
  
	public int getNextPlayer() {
		return GameController.nextPlayerIndex;
	}
	
	public void setNextPlayer(int nextPlayer) {
		GameController.nextPlayerIndex = nextPlayer;
	}
	
	public void AdvanceToNextTurn() {
		
		GameSettings.SetGameIsOver(false);
		
		//before advancing, check to see if the current player has (just) won the game.
		if(currentPlayer.PlayerHasWon()) {		
			
			//pop up a message for quit/new game here.
			GameSettings.getInGameUIPanel().getWinnerLabel().setText(currentPlayer.GetName()+
								" is the winner!");
			
			//pause the game, lock the controls (activated when player clicks on message)
			GameSettings.SetGameIsPaused(true);
			GameSettings.SetGameIsOver(true);
			return;
		}
		
		//get the next player to play
		Player nextPlayer = allPlayers[nextPlayerIndex];				
		//System.out.println("Advancing to next turn. Next player is " +nextPlayer.GetName());
		
		//loop through the tiles, deactivate all move-hint highlights
		for(int x = 0; x < GameSettings.GetRows(); x++) {
			for(int y = 0; y < GameSettings.GetCols(); y++) {				
				tiles[x][y].DeactivateTile();				
			}
		}
		
		//turn on the click-when-ready button in message panel
		GameSettings.getInGameUIPanel().setOkButtonIsVisible(true);
		
		//deactivate the goal tile highights for the next player.
		for(GameTile thisTile : goalTiles) {
			thisTile.DeactivateGoalPanel();
		}
		goalTiles.clear();	 		
		
		//pause the game, lock the controls (activated when player clicks on message)
		GameSettings.SetGameIsPaused(true);		
		GameSettings.getInGameUIPanel().setMessageLabelText("Your Turn, " +nextPlayer.GetName());
		GameSettings.getInGameUIPanel().showMessagelabel();
		GameSettings.getInGameUIPanel().getOkButton().setVisible(true);
		GameSettings.getInGameUIPanel().getOkButton().setEnabled(true);
		currentPlayer = nextPlayer;
    
    //current player will BeginTurn()
		currentPlayer = nextPlayer;
		//increment the nextPlayerIndex, so the turns will advance through different players
		nextPlayerIndex++;
		//if past end, loop back to start
		if(nextPlayerIndex >3) {
			nextPlayerIndex = 0;
		}
	}
  
	public void BeginTurn() {

		//activate the goal tiles for this player (show a gold star icon)
		ActivateGoalTiles(currentPlayer);
		GameSettings.setGoalTiles(goalTiles);
		
		//get a structure to hold the gametiles that can be reached by player.
		ArrayList<GameTile> legalTiles = new ArrayList<GameTile>();
		
		
		
		//Handle AI here
		if(currentPlayer.GetType().equals("c")) {
			//this is a computer player
			legalTiles = FindLegalTiles(currentPlayer);
			ActivateLegalTiles(legalTiles);
			AIManager.PerformAITurn(currentPlayer, legalTiles);				
		}else {
			//this player is a human
			//allow tracking of input
			GameSettings.SetGameIsPaused(false);		
			//System.out.println("game paused? " +GameSettings.GetGameIsPaused());
			//System.out.println("Starting turn for " +currentPlayer.GetName());
			//it is now "currentPlayer"'s turn
			legalTiles = FindLegalTiles(currentPlayer);
			ActivateLegalTiles(legalTiles);
		}
	}
	
	private void ActivateGoalTiles(Player thisPlayer) {
		
		String goal = thisPlayer.GetPlayerGoal();
		
		int row = 0;
		int col = 0;
		
		switch(goal) {
		
		case "north":
			//get the northern border tiles
			row = 0;
			for(int i = 0; i < GameSettings.GetCols(); i++) {
				goalTiles.add(tiles[row][i]);
			}
			break;
			
		case "east":
			//get the eastern border tiles
			col = GameSettings.GetCols() - 1;
			for(int i = 0; i < GameSettings.GetRows(); i++) {
				goalTiles.add(tiles[i][col]);
			}
			break;
			
		case "south":
			//get the southern border tiles
			row = GameSettings.GetRows() - 1;
			for(int i = 0; i < GameSettings.GetCols(); i++) {
				goalTiles.add(tiles[row][i]);
			}
			break;
			
		case "west":
			//get the western border tiles
			col = 0;
			for(int i = 0; i < GameSettings.GetRows(); i++) {
				goalTiles.add(tiles[i][col]);
			}
			break;		
		}
		
		for(int i = 0; i < GameSettings.GetRows(); i++) {
			//an appropriate number of goal panels were created in this constructor.
			goalTiles.get(i).ActivateGoalPanel(goalPanels.get(i));
		}
		
	}
	
	private ArrayList<GameTile> FindLegalTiles(Player currentPlayer) {
		
		//move this to GameBoard class?
		
		//find a collection of tiles that the player can move to, and return them.
		
		ArrayList<GameTile> legalTiles = new ArrayList<GameTile>();
		
		//get the 2D array of gametiles
		GameTile[][] tiles = GameSettings.getGameTiles();
		
		//get the number of rows and cols on gameboard
		//we need this zero indexed, so subtract one.
		//int rows = GameSettings.GetRows() - 1; 
		//int cols = GameSettings.GetCols() - 1;
		//get the gameTile that the player is standing on		
		GameTile location = currentPlayer.GetTile();
		//get the x, y, address of this tile
		int rowAddress = location.GetXCoord();
		int colAddress = location.GetYCoord();		
		
		//System.out.println(currentPlayer.GetName() + " is on tile (" +rowAddress+ ", " +colAddress+ ")");
		
		//does player have north option?
		if(location.HasNorthTile() && !location.HasNorthWall()) {
			
			//get the north tile
			GameTile northTile = tiles[rowAddress-1][colAddress];			
			//see if another player is on north tile
			Boolean opponentOnNorth = (northTile.playerIsHere) ? true : false;
			if(opponentOnNorth) {
				//see if northTile has a north tile and no north wall
				//player can possibly jump over opponent
				if(northTile.HasNorthTile() && !northTile.HasNorthWall()) {
					GameTile jumpTile = tiles[rowAddress-2][colAddress];
					//if there is no player present on the jump tile, activate it
					if(!jumpTile.PlayerIsHere()) {
						legalTiles.add(jumpTile);						
					}					
				}
				else {
					//see if northTile has a north wall (player may move diagonally)
					if(northTile.HasNorthWall()) {
						GameTile cornerTile;
						//check north east corner
						if(northTile.HasEastTile() && !northTile.HasEastWall()) {
							cornerTile = tiles[rowAddress-1][colAddress+1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
						//check north west corner
						if(northTile.HasWestTile() && !northTile.HasWestWall()) {
							cornerTile = tiles[rowAddress-1][colAddress-1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
					}					
				}
			}else {
				//north tile is reachable, and is empty
				legalTiles.add(northTile);
			}			
		}
		
		//does player have east option?
		if(location.HasEastTile() && !location.HasEastWall()) {
			
			//get the east tile
			GameTile eastTile = tiles[rowAddress][colAddress+1];			
			//see if another player is on east tile
			Boolean opponentOnEast = (eastTile.playerIsHere) ? true : false;
			if(opponentOnEast) {
				//see if eastTile has a east tile and no east wall
				//player can possibly jump over opponent
				if(eastTile.HasEastTile() && !eastTile.HasEastWall()) {
					GameTile jumpTile = tiles[rowAddress][colAddress+2];
					//if there is no player present on the jump tile, activate it
					if(!jumpTile.PlayerIsHere()) {
						legalTiles.add(jumpTile);
					}					
				}
				else {
					//see if eastTile has a east wall (player may move diagonally)
					if(eastTile.HasEastWall()) {
						GameTile cornerTile;
						//check north east corner
						if(eastTile.HasNorthTile() && !eastTile.HasNorthWall()) {
							cornerTile = tiles[rowAddress-1][colAddress+1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
						//check south east corner
						if(eastTile.HasSouthTile() && !eastTile.HasSouthWall()) {
							cornerTile = tiles[rowAddress+1][colAddress+1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
					}					
				}
			}else {
				//east tile is reachable, and is empty
				legalTiles.add(eastTile);
			}			
		}
				
		//does player have south option?
		if(location.HasSouthTile() && !location.HasSouthWall()) {
			
			//get the south tile
			GameTile southTile = tiles[rowAddress+1][colAddress];			
			//see if another player is on south tile
			Boolean opponentOnSouth = (southTile.playerIsHere) ? true : false;
			if(opponentOnSouth) {
				//see if southTile has a south tile and no south wall
				//player can possibly jump over opponent
				if(southTile.HasSouthTile() && !southTile.HasSouthWall()) {
					GameTile jumpTile = tiles[rowAddress+2][colAddress];
					//if there is no player present on the jump tile, activate it
					if(!jumpTile.PlayerIsHere()) {
						legalTiles.add(jumpTile);
					}					
				}
				else {
					//see if southTile has a south wall (player may move diagonally)
					if(southTile.HasSouthWall()) {
						GameTile cornerTile;
						//check south east corner
						if(southTile.HasEastTile() && !southTile.HasEastWall()) {
							cornerTile = tiles[rowAddress+1][colAddress+1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
						//check south west corner
						if(southTile.HasWestTile() && !southTile.HasWestWall()) {
							cornerTile = tiles[rowAddress+1][colAddress-1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
					}					
				}
			}else {
				//north tile is reachable, and is empty
				legalTiles.add(southTile);
			}			
		}
				
		//does player have west option?
		if(location.HasWestTile() && !location.HasWestWall()) {
			
			//get the west tile
			GameTile westTile = tiles[rowAddress][colAddress-1];			
			//see if another player is on west tile
			Boolean opponentOnWest = (westTile.playerIsHere) ? true : false;
			if(opponentOnWest) {
				//see if westTile has a west tile and no west wall
				//player can possibly jump over opponent
				if(westTile.HasWestTile() && !westTile.HasWestWall()) {
					GameTile jumpTile = tiles[rowAddress][colAddress-2];
					//if there is no player present on the jump tile, activate it
					if(!jumpTile.PlayerIsHere()) {
						legalTiles.add(jumpTile);
					}					
				}
				else {
					//see if westTile has a west wall (player may move diagonally)
					if(westTile.HasWestWall()) {
						GameTile cornerTile;
						//check north west corner
						if(westTile.HasNorthTile() && !westTile.HasNorthWall()) {
							cornerTile = tiles[rowAddress-1][colAddress-1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
						//check south west corner
						if(westTile.HasSouthTile() && !westTile.HasSouthWall()) {
							cornerTile = tiles[rowAddress+1][colAddress-1];
							//if there is no player present on the corner tile, activate it
							if(!cornerTile.PlayerIsHere()) {
								legalTiles.add(cornerTile);
							}
						}
					}					
				}
			}else {
				//west tile is reachable, and is empty
				legalTiles.add(westTile);
			}			
		}
		
		//return the collection of legal tiles
		return legalTiles;
	}
	
	private void ActivateLegalTiles(ArrayList<GameTile> reachableTiles) {
		for(GameTile tile : reachableTiles) {
			tile.ActivateTile();
		}
	}
}

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

/*
 * This class handles AI turn decision and movement.
 * 
 * AI has been split into two difficulty levels: easy, and hard.
 * 
 * Easy AI will move and place walls more randomly, but will generally attempt to move towards the goal.
 * Hard AI will place walls aggressively, and will take a more direct path to the finish.
 * 
 */
public class AIManager {
	
	public static void PerformAITurn(Player currentPlayer, ArrayList<GameTile> legalTiles) {
		
		//easy AI will attempt to either move or place a wall.
		
		//should be lateral or forwards when possible.  Avoid going backwards.
		//when placing a wall, try to keep them near center of the board
		
		//set liklihood of choosing move or wall
		//using a random generator between 1-100.  
		//Lower numbers = move pawn.  Higher numbers = place wall.  50 = equal liklihood of move/wall
		int chanceIndex;  		
		if(currentPlayer.IsDifficult()) {
			//do hard AI
			//probability of placing a wall
			chanceIndex = 50;
			//get a random int between 0 and 100
			Random randomizer = new Random();
			int turnDecision = randomizer.nextInt(100);			
			if(turnDecision >= chanceIndex) {
				//try to place a wall
				//place a wall which blocks the path of closest player to the goal.			
				PlaceHardWall(currentPlayer, legalTiles);			
			}
			else {
				//try to move the pawn closer to the goal (only!)			
				MakeHardMove(currentPlayer, legalTiles);
			}			
		}
		else {
			//do easy AI			
			//probability of placing a wall
			chanceIndex = 50;
			//get a random int between 0 and 100
			Random randomizer = new Random();
			int turnDecision = randomizer.nextInt(100);			
			if(turnDecision >= chanceIndex) {
				//try to place a wall
				//pick a tile somewhere near the middle of the board, and turn on a wall in random direction.			
				PlaceEasyWall(currentPlayer, legalTiles);			
			}
			else {
				//try to move the pawn, generally closer to goal, may be random.			
				MakeEasyMove(currentPlayer, legalTiles);
			}			
		}
	}
	
	//code below mostly borrowed from InputManager.  Should be refactored if there's time.
	private static void MakeEasyMove(Player currentPlayer, ArrayList<GameTile> legalTiles) {
		
		//strategy:  Easy AI will calculate two possibilities for a move destination.
		// possibility 1:  the tile, from legal tiles, which moves player closest to the goal.
		// possibility 2:  a random tile from the legal tiles list.
		
		//then, there is a random chance associated with picking one of these 2 options.
		//pick one, according to chance, and use that tile as the destination for move.
		
		GameTile destinationTile;
		
		//if there are no tiles, throw error.
		if(legalTiles.size() == 0 || legalTiles == null) {
			System.out.println("AI has no legal tiles to choose from!");
			return;
		}
		
		//if there is only one tile available, choose that and return
		if(legalTiles.size() == 1) {
			destinationTile = legalTiles.get(0);
			//check that this tile can accept the player.
			if(destinationTile.CanAcceptPlayer()) {							
				//remove them from the tile they are standing on
				currentPlayer.GetTile().RemovePlayer();
				//add them to the tile that was selected
				destinationTile.AddPlayer(currentPlayer);
				//update the player's location attribute to store new location.
				currentPlayer.setTile(destinationTile);							
			}else {
				System.out.println("AI cannot move to the only destination tile available!");
			}
		}				
		
		//otherwise, loop legal tiles looking for a destination
		Boolean tileFound = false;
		while(!tileFound) {
			
			//find closest tile to goal
			//get goal direction.  A string with north, east, south or west.
			String goalDirection = currentPlayer.GetPlayerGoal();
			int distanceToGoal = 999;
			int shortestSoFar = 999;
			GameTile closestTile = legalTiles.get(0);
			//loop legalTiles, find the closest tile to the goal
			for(GameTile thisTile : legalTiles) {
				int rowGoal;
				int colGoal;
				switch (goalDirection) {
				case "north":
					rowGoal = 0;
					distanceToGoal = thisTile.GetXCoord() - rowGoal;
					break;
				
				case "east":
					colGoal = GameSettings.GetCols() - 1;
					distanceToGoal = colGoal - thisTile.GetYCoord();				
					break;
				
				case "south":
					rowGoal = GameSettings.GetRows() - 1;
					distanceToGoal = rowGoal - thisTile.GetXCoord();				
					break;
					
				case "west":
					colGoal = 0;
					distanceToGoal = thisTile.GetYCoord() - colGoal;				
					break;		
				default:
					System.out.println("AI could not determine player goal!");
				}
				
				// update shortest distance if closer, keep closest tile.
				if(distanceToGoal < shortestSoFar) {
					shortestSoFar = distanceToGoal;
					closestTile = thisTile;				
				}			
			}
			
			//we now have closest tile, the "best" possible move (not necessarily! but it's likely ok.)
			
			//next, as another option, get a random tile from the list
			Random randomizer = new Random();
			GameTile randomTile = legalTiles.get(randomizer.nextInt(legalTiles.size()));
			
			//now, set a probability that AI will choose closest tile, or random tile
			//can adjust this. 0-100. higher = more prob of taking a "best" move.
			int chooseClosestProbability = 60;
			int randomNumber = randomizer.nextInt(100);			
			
			if(randomNumber <= chooseClosestProbability) {
				//select the closest tile to the goal
				destinationTile = closestTile;
			}
			else {
				//select random tile
				destinationTile = randomTile;
			}
			
			//check that this tile can accept the player.  if so, make move, otherwise repeat while loop.
			if(destinationTile.CanAcceptPlayer()) {							
				//remove them from the tile they are standing on
				currentPlayer.GetTile().RemovePlayer();
				//add them to the tile that was selected
				destinationTile.AddPlayer(currentPlayer);
				//update the player's location attribute to store new location.
				currentPlayer.setTile(destinationTile);
				//break while loop
				tileFound = true;			
			}
		}
					
		//end the turn and advance to the next turn.
		GameSettings.GetGameController().AdvanceToNextTurn();		
	}

	private static void PlaceEasyWall(Player currentPlayer, ArrayList<GameTile> legalTiles) {
		Boolean foundLocation = false;
		JPanel[] tempWalls = new JPanel[8];
		int maxTries = 20;
		int tryCount = 0;
		while(!foundLocation && (tryCount < maxTries)) {
			
			//SHOULD PUT A COUNTER HERE.  
			//let computer try for 50-100 iterations. if still no wall placement avail, move instead.
			
			//board ranges from (0,0) to (rows,cols). 
			int max = GameSettings.GetRows() - 2;
			int min = 2;
			//get ints to create a address between max and min
			int x = (int)(Math.random() * (max - min)) + min;
			int y = (int)(Math.random() * (max - min)) + min;
			
			//get GameTile at this address
			GameTile thisTile = GameSettings.getGameTiles()[x][y];
			
			//get a number from 1-8 to represent choosing from 8 different wall possibilities on this tile
			int wallPosition = (int)(Math.random() * (8 - 1)) + 1;
			
			//see if we can get a temp array of valid panels
			tempWalls = AttemptWallHere(thisTile, wallPosition);
			
			if(tempWalls[0] == null) {
				//this set is invalid.  repeat while loop.
				foundLocation = false;
			}
			//check if this wall would block any player from reaching goal
			else if(CheckPath.ThisWallBlocksGoal(tempWalls)) {
				foundLocation = false;
			}
			else {
				//found a valid set for display.  break while loop
				foundLocation = true;
			}
			//increase num tries.  if too many, break while loop.
			tryCount++;
		}
		
		//check if we found a location, or failed on numTries.  
		//if failed on numTries, attempt to move instead.
		if(!foundLocation) {
			MakeEasyMove(currentPlayer, legalTiles);
			return;
		}
		
		//found a suitable set of 8 panels for a wall.  ok to place.
		// set the wall color of each panel in the tempWall[] to display it
		ArrayList<JPanel> panelList = new ArrayList<JPanel>();
		for (JPanel thisPanel : tempWalls) {
			if (thisPanel != null) {
				// find the parent (the GameTile obj) that is responsible for this panel
				GameTile parent = (GameTile) thisPanel.getParent();
				// parent activates the wall (changes it's color to wall)
				parent.ActivateWallPanel(thisPanel);
				//parent locks the wall so it cannot be removed
				parent.LockWall(thisPanel);
				//add it to an array list
				panelList.add(thisPanel);
				// repeat until 8 walls have been activated.
			}
		}
		//append these walls to the InputManager's list of all locked walls
		GameSettings.GetInputManager().AddLockedWalls(panelList);
		
		//decrement the player's wall inventory
		//subtract one from the player's wall inventory
		currentPlayer.setWallsRemaining(currentPlayer.GetWallsRemaining() - 1);
		GameSettings.getInGameUIPanel().UpdatePlayerInfoDisplay();
		//System.out.println(currentPlayer.GetName() + " placed a wall.  They have " +currentPlayer.GetWallsRemaining()+ " walls remining.");
		
		//end the turn.
		GameSettings.GetGameController().AdvanceToNextTurn();		
		
	}
		
	private static void MakeHardMove(Player currentPlayer, ArrayList<GameTile> legalTiles){
		
		//Similar to easy move, execept AI should only attempt to move to closest tile in direction of target
		//easy move sometimes picks random tile, this time no random selection
		
		//if there are no tiles, throw error.
		if(legalTiles.size() == 0 || legalTiles == null) {
			System.out.println("AI has no legal tiles to choose from!");
			return;
		}
		
		GameTile closestTile = legalTiles.get(0);
		
		//if there is only one tile available, choose that and return
		if(legalTiles.size() == 1) {
			closestTile = legalTiles.get(0);
		}
		else {
			//there is more than one option.  find closest.
			//find closest tile to goal
			//get goal direction.  A string with north, east, south or west.
			String goalDirection = currentPlayer.GetPlayerGoal();
			int distanceToGoal = 999;
			int shortestSoFar = 999;
			//loop legalTiles, find the closest tile to the goal
			for(GameTile thisTile : legalTiles) {
				int rowGoal;
				int colGoal;
				switch (goalDirection) {
				case "north":
					rowGoal = 0;
					distanceToGoal = thisTile.GetXCoord() - rowGoal;
					break;
				
				case "east":
					colGoal = GameSettings.GetCols() - 1;
					distanceToGoal = colGoal - thisTile.GetYCoord();				
					break;
				
				case "south":
					rowGoal = GameSettings.GetRows() - 1;
					distanceToGoal = rowGoal - thisTile.GetXCoord();				
					break;
					
				case "west":
					colGoal = 0;
					distanceToGoal = thisTile.GetYCoord() - colGoal;				
					break;		
				default:
					System.out.println("AI could not determine player goal!");
				}
				
				// update shortest distance if closer, keep closest tile.
				if(distanceToGoal < shortestSoFar) {
					shortestSoFar = distanceToGoal;
					closestTile = thisTile;				
				}			
			}			
		}
		
		//we now have closest tile, the "best" possible move (not necessarily! but it's likely ok.)
		//remove them from the tile they are standing on
		currentPlayer.GetTile().RemovePlayer();
		//add them to the tile that was selected
		closestTile.AddPlayer(currentPlayer);
		//update the player's location attribute to store new location.
		currentPlayer.setTile(closestTile);
		
		//end the turn and advance to the next turn.
		GameSettings.GetGameController().AdvanceToNextTurn();	
	}
	
	private static void PlaceHardWall(Player currentPlayer, ArrayList<GameTile> legalTiles) {
		//find the closest player to the goal.  Place a wall directly in front of that player if possible.
		
		//get ref to all players
		Player[] allPlayers = GameSettings.getPlayers();
		
		//get an array to hold the panels that comprise the wall about to be placed
		JPanel[] tempWalls = new JPanel[8];
		
		int distanceToGoal = 999;
		int shortestSoFar = 999;
		String opponentGoalDirection;
		int rowGoal;
		int colGoal;
		Player closestPlayerToGoal = allPlayers[0];
		
		//find the player with the shortest distance to their goal
		for(Player thisPlayer : allPlayers) {
			
			//ignore the player that is currently making a turn
			if(thisPlayer == currentPlayer) {
				continue;
			}			
			
			opponentGoalDirection = thisPlayer.GetPlayerGoal();
			switch (opponentGoalDirection) {
			case "north":
				rowGoal = 0;
				distanceToGoal = thisPlayer.GetTile().GetXCoord() - rowGoal;
				break;
			
			case "east":
				colGoal = GameSettings.GetCols() - 1;
				distanceToGoal = colGoal - thisPlayer.GetTile().GetYCoord();				
				break;
			
			case "south":
				rowGoal = GameSettings.GetRows() - 1;
				distanceToGoal = rowGoal - thisPlayer.GetTile().GetXCoord();				
				break;
				
			case "west":
				colGoal = 0;
				distanceToGoal = thisPlayer.GetTile().GetYCoord() - colGoal;				
				break;		
			default:
				System.out.println("AI could not determine player goal!");
			}
			
			// update shortest distance if closer, keep closest player to goal.
			if(distanceToGoal < shortestSoFar) {
				shortestSoFar = distanceToGoal;
				closestPlayerToGoal = thisPlayer;				
			}	
		}
		
		//we now know the closest player to the goal (the biggest threat to currentPlayer)
		//try and block that player with a wall in the appropriate direction.
		//if there is already a wall between closest player and goal, this player should attempt to move towards it's goal instead.
		opponentGoalDirection = closestPlayerToGoal.GetPlayerGoal();
		//Boolean wallPlaced = false;
		Random randomizer = new Random();
		int direction = 0;
		switch (opponentGoalDirection) {
		case "north":
			//opponent is heading north.
			//try to place a north wall.  pick a side, 0 = left or 1 = right
			randomizer = new Random();
			direction = randomizer.nextInt(1);
			if(direction == 0) {
				//try left first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 1);
				if(tempWalls[0] == null) {
					//try right
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 3);
				}								
			}
			else {
				//try right first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 3);
				if(tempWalls[0] == null) {
					//try left
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 1);
				}
			}
			
			break;
		
		case "east":
			//opponent is heading east.
			//try to place a east wall.  pick a side, 0 = upper or 1 = lower
			randomizer = new Random();
			direction = randomizer.nextInt(1);
			if(direction == 0) {
				//try upper first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 4);
				if(tempWalls[0] == null) {
					//try lower
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 8);
				}								
			}
			else {
				//try lower first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 8);
				if(tempWalls[0] == null) {
					//try upper
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 4);
				}
			}
			
			break;
		
		case "south":
			//opponent is heading south.
			//try to place a south wall.  pick a side, 0 = left or 1 = right
			randomizer = new Random();
			direction = randomizer.nextInt(1);
			if(direction == 0) {
				//try left first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 5);
				if(tempWalls[0] == null) {
					//try right
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 7);
				}								
			}
			else {
				//try right first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 7);
				if(tempWalls[0] == null) {
					//try left
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 5);
				}
			}
			
			break;
			
		case "west":
			//opponent is heading west.
			//try to place a west wall.  pick a side, 0 = upper or 1 = lower
			randomizer = new Random();
			direction = randomizer.nextInt(1);
			if(direction == 0) {
				//try upper first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 2);
				if(tempWalls[0] == null) {
					//try lower
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 6);
				}								
			}
			else {
				//try lower first
				tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 6);
				if(tempWalls[0] == null) {
					//try upper
					tempWalls = AttemptWallHere(closestPlayerToGoal.GetTile(), 2);
				}
			}
			
			break;		
		default:
			System.out.println("AI could not determine player goal!");
		}
		
		//we now have a collection for tempWalls.
		//they are either 8 valid panels, or 8 nulls.  check.
		if(tempWalls[0] == null) {
			//could not place a wall to block player.  move instead.
			MakeHardMove(currentPlayer, legalTiles);
			return;
		}
		//check to see if this wall would completely block a player
		else if(CheckPath.ThisWallBlocksGoal(tempWalls)) {
			//this wall would block at least one player. move instead.
			MakeHardMove(currentPlayer, legalTiles);
			return;
		}		
		else {
			//the AI can place wall in front of the closest player to the goal.  
			//place wall and end turn.
			ArrayList<JPanel> panelList = new ArrayList<JPanel>();
			for (JPanel thisPanel : tempWalls) {
				if (thisPanel != null) {
					// find the parent (the GameTile obj) that is responsible for this panel
					GameTile parent = (GameTile) thisPanel.getParent();
					// parent activates the wall (changes it's color to wall)
					parent.ActivateWallPanel(thisPanel);
					//parent locks the wall so it cannot be removed
					parent.LockWall(thisPanel);
					//add it to an array list
					panelList.add(thisPanel);
					// repeat until 8 walls have been activated.
				}
			}
			//append these walls to the InputManager's list of all locked walls
			GameSettings.GetInputManager().AddLockedWalls(panelList);
			
			//decrement the player's wall inventory
			//subtract one from the player's wall inventory			
			currentPlayer.setWallsRemaining(currentPlayer.GetWallsRemaining() - 1);
			GameSettings.getInGameUIPanel().UpdatePlayerInfoDisplay();
			//System.out.println(currentPlayer.GetName() + " placed a wall.  They have " +currentPlayer.GetWallsRemaining()+ " walls remining.");
			//end the turn.
			GameSettings.GetGameController().AdvanceToNextTurn();
			return;
		}		
	}

	//helper method
	//Params:
	//	thisTile = the game tile to place a wall on.
	//	wallPosition:	1 = north wall, left		2 = west wall, upper
	//					3 = north wall, right		4 = east wall, upper
	//					5 = south wall, left		6 = west wall, lower
	//					7 = south wall, right		8 = east wall, lower
	private static JPanel[] AttemptWallHere(GameTile thisTile, int wallPosition) {
				
		//return JPanel array with 8 panels if successful, or array with 8 nulls if unsuccecssful.
				
		//build temporary wall according to wallPosition arg
		//check if any parts of temp wall overlap existing walls in allLockedPanels
		//if no overlap with existing walls, temp is valid.  Place as a wall and return true. otherwise return false.
		//try and place a wall at this position
		
		//get ref to all the game tiles
		GameTile[][] gridTiles = GameSettings.getGameTiles();
		
		//get the x,y for thisTile
		int x = thisTile.GetXCoord();
		int y = thisTile.GetYCoord();
		
		//get all walls that are currently in play
		ArrayList<JPanel> allLockedWalls = GameSettings.GetInputManager().getAllLockedWalls();
		
		//get an array to hold the panels that comprise the wall to be placed
		JPanel[] tempWalls = new JPanel[8];				
				
		switch (wallPosition) {			

		case 1:
			// if no north neighbour, or no west neighbour, break.
			if(!thisTile.HasNorthTile() || !thisTile.HasWestTile()) {
				break;
			}
			// light-up top wall/corner on this tile, and top on west neighbour
			// also bottom on north neighbour, and bottom on nw neighbour.
			// this
			tempWalls[0] = gridTiles[x][y].GetTop();
			tempWalls[1] = gridTiles[x][y].GetTopLeft();
			// west
			tempWalls[2] = gridTiles[x][y - 1].GetTopRight();
			tempWalls[3] = gridTiles[x][y - 1].GetTop();
			// north
			tempWalls[4] = gridTiles[x - 1][y].GetBottom();
			tempWalls[5] = gridTiles[x - 1][y].GetBottomLeft();
			// northwest
			tempWalls[6] = gridTiles[x - 1][y - 1].GetBottomRight();
			tempWalls[7] = gridTiles[x - 1][y - 1].GetBottom();
			break;

		case 2:
			// if no north neighbour, no west neighbour, break.
			if (!thisTile.HasNorthTile() || !thisTile.HasWestTile()) {
				break;
			}
			// light-up left wall/corner on this tile, and right on west neighbour
			// also left on north neighbour, and right on nw neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetLeft();
			tempWalls[1] = gridTiles[x][y].GetTopLeft();
			// west
			tempWalls[2] = gridTiles[x][y - 1].GetRight();
			tempWalls[3] = gridTiles[x][y - 1].GetTopRight();
			// north
			tempWalls[4] = gridTiles[x - 1][y].GetLeft();
			tempWalls[5] = gridTiles[x - 1][y].GetBottomLeft();
			// northwest
			tempWalls[6] = gridTiles[x - 1][y - 1].GetRight();
			tempWalls[7] = gridTiles[x - 1][y - 1].GetBottomRight();
			break;

		case 3:
			// if no north neighbour, or no east neighbour, break
			if (!thisTile.HasNorthTile() || !thisTile.HasEastTile()) {
				break;
			}
			// light-up top wall/corner on this tile, and bottom on north neighbour
			// also top on east neighbour, and bottom on ne neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetTop();
			tempWalls[1] = gridTiles[x][y].GetTopRight();
			// north
			tempWalls[2] = gridTiles[x - 1][y].GetBottom();
			tempWalls[3] = gridTiles[x - 1][y].GetBottomRight();
			// east
			tempWalls[4] = gridTiles[x][y + 1].GetTop();
			tempWalls[5] = gridTiles[x][y + 1].GetTopLeft();
			// northeast
			tempWalls[6] = gridTiles[x - 1][y + 1].GetBottom();
			tempWalls[7] = gridTiles[x - 1][y + 1].GetBottomLeft();
			break;

		case 4:
			// if no north neighbour, or no east neighbour, break
			if (!thisTile.HasNorthTile() || !thisTile.HasEastTile()) {
				break;
			}
			// light-up right wall/corner on this tile, and right on north neighbour
			// also left on east neighbour, and left on ne neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetRight();
			tempWalls[1] = gridTiles[x][y].GetTopRight();
			// north
			tempWalls[2] = gridTiles[x - 1][y].GetRight();
			tempWalls[3] = gridTiles[x - 1][y].GetBottomRight();
			// east
			tempWalls[4] = gridTiles[x][y + 1].GetLeft();
			tempWalls[5] = gridTiles[x][y + 1].GetTopLeft();
			// northeast
			tempWalls[6] = gridTiles[x - 1][y + 1].GetLeft();
			tempWalls[7] = gridTiles[x - 1][y + 1].GetBottomLeft();
			break;

		case 5:
			// if no south neighbour, or no west neighbour, break
			if (!thisTile.HasSouthTile() || !thisTile.HasWestTile()) {
				break;
			}
			// light-up bottom wall/corner on this tile, and top on south neighbour
			// also bottom on west neighbour, and top on sw neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetBottom();
			tempWalls[1] = gridTiles[x][y].GetBottomLeft();
			// south
			tempWalls[2] = gridTiles[x + 1][y].GetTop();
			tempWalls[3] = gridTiles[x + 1][y].GetTopLeft();
			// west
			tempWalls[4] = gridTiles[x][y - 1].GetBottom();
			tempWalls[5] = gridTiles[x][y - 1].GetBottomRight();
			// southwest
			tempWalls[6] = gridTiles[x + 1][y - 1].GetTop();
			tempWalls[7] = gridTiles[x + 1][y - 1].GetTopRight();
			break;

		case 6:
			// if no south neighbour, or west neighbour, break
			if (!thisTile.HasSouthTile() || !thisTile.HasWestTile()) {
				break;
			}
			// light-up left wall/corner on this tile, and left on south neighbour
			// also right on west neighbour, and right on sw neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetLeft();
			tempWalls[1] = gridTiles[x][y].GetBottomLeft();
			// south
			tempWalls[2] = gridTiles[x + 1][y].GetLeft();
			tempWalls[3] = gridTiles[x + 1][y].GetTopLeft();
			// west
			tempWalls[4] = gridTiles[x][y - 1].GetRight();
			tempWalls[5] = gridTiles[x][y - 1].GetBottomRight();
			// southwest
			tempWalls[6] = gridTiles[x + 1][y - 1].GetRight();
			tempWalls[7] = gridTiles[x + 1][y - 1].GetTopRight();
			break;

		case 7:
			// if no south neighbour, or east neighbour, break
			if (!thisTile.HasSouthTile() || !thisTile.HasEastTile()) {
				break;
			}
			// light-up bottom wall/corner on this tile, and top on south neighbour
			// also bottom on east neighbour, and top on se neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetBottom();
			tempWalls[1] = gridTiles[x][y].GetBottomRight();
			// south
			tempWalls[2] = gridTiles[x + 1][y].GetTop();
			tempWalls[3] = gridTiles[x + 1][y].GetTopRight();
			// east
			tempWalls[4] = gridTiles[x][y + 1].GetBottom();
			tempWalls[5] = gridTiles[x][y + 1].GetBottomLeft();
			// southeast
			tempWalls[6] = gridTiles[x + 1][y + 1].GetTop();
			tempWalls[7] = gridTiles[x + 1][y + 1].GetTopLeft();
			break;

		case 8:
			// if no south neighbour, or east neighbour, break
			if (!thisTile.HasSouthTile() || !thisTile.HasEastTile()) {
				break;
			}
			// light-up right wall/corner on this tile, and right on south neighbour
			// also left on east neighbour, and left on se neighbour.
			// this tile
			tempWalls[0] = gridTiles[x][y].GetRight();
			tempWalls[1] = gridTiles[x][y].GetBottomRight();
			// south
			tempWalls[2] = gridTiles[x + 1][y].GetRight();
			tempWalls[3] = gridTiles[x + 1][y].GetTopRight();
			// east
			tempWalls[4] = gridTiles[x][y + 1].GetLeft();
			tempWalls[5] = gridTiles[x][y + 1].GetBottomLeft();
			// southeast
			tempWalls[6] = gridTiles[x + 1][y + 1].GetLeft();
			tempWalls[7] = gridTiles[x + 1][y + 1].GetTopLeft();
			break;

		default:				
			System.out.println("no match for AI locationCode");
		}
		
		// we now have a set of temporary walls to display.
		// if any of them are locked, discard the set
		for (JPanel panelToCheck : tempWalls) {
			if (allLockedWalls.contains(panelToCheck)) {
				// clear the array
				for (int i = 0; i < tempWalls.length; i++) {
					tempWalls[i] = null;
				}
				//array is empty, break loop
				break;
			}
		}
		//return an array with 8 valid panels, or 8 nulls.
		return tempWalls;
	}
	
}



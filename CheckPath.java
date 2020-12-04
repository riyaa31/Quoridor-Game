import java.util.ArrayList;

import javax.swing.JPanel;

public class CheckPath {
	
	//User has requested to place a wall from mouseover-click.
	
	//Check this request to see if it prevents ANY player from reaching ALL of their goal tiles.
	//If a requested wall blocks any player from their entire goal (9 tile collection), it is denied.
	
	//uses a floodfill algorithim to collect all possible reachable tiles.
	//if no goal tiles are found when finished, the goal could not be reached, and wall request is rejected.
	
	//ref to the gameboard
	private static GameTile[][] tiles;
	
	//var for holding a subset of all game tiles.
	//filled with false, addresses set true if visited via recursive method.
	private static Boolean[][] visitedTiles;
	
	
	//public method for testing existance of path-to-goal
	//Param:   tempWalls = the collection of temporary panels, describing a wall, that the player wishes to place.
	public static Boolean ThisWallBlocksGoal(JPanel[] tempWalls) {
		
		Boolean pathIsBlocked = true;  		//assume true, prove false.
		
		//get reference to the gameboard tiles
		tiles = GameSettings.getGameTiles();
		
		//get ref for players
		Player[] allPlayers = GameSettings.getPlayers();
		
		//for each player, do a flood fill from player's position.
		//use visitedTiles like a map, and set true for every position visited at least once.
		for(Player thisPlayer : allPlayers) {
			
			//this table of bools is the same size as the game board.
			//initialize this visitedTiles map as all "false" (unvisited).
			visitedTiles = new Boolean[GameSettings.GetRows()][GameSettings.GetCols()];
			for (int x = 0; x < GameSettings.GetRows(); x++) {
				for (int y = 0; y < GameSettings.GetCols(); y++) {
					visitedTiles[x][y] = false;
				}
			}
						
			//get the player's position
			int x = thisPlayer.GetTile().GetXCoord();
			int y = thisPlayer.GetTile().GetYCoord();
			
			//use flood-fill recursion to update map of visited tiles
			CheckAllPaths(x, y, tempWalls);
			
			//visited tiles, a boolean map, now shows "true" for each tile that was visited.
			//check to see if player's goal tiles are among the list of visited tiles.
			
			//get the player's goal direction, build a collection of goal tiles.
			//player must be able to reach at least one of these tiles.
			String playerGoalDirection = thisPlayer.GetPlayerGoal();
			ArrayList<GameTile> playerGoalTiles = new ArrayList<GameTile>();
			
			switch (playerGoalDirection) {
			case "north":
				//add all north edge tiles to array list
				for(int i = 0; i < GameSettings.GetCols()-1; i++) {
					playerGoalTiles.add(tiles[0][i]);
				}				
				break;
			
			case "east":
				//add all east edge tiles to array list
				for(int i = 0; i < GameSettings.GetRows()-1; i++) {
					playerGoalTiles.add(tiles[i][GameSettings.GetCols()-1]);
				}				
				break;
			
			case "south":
				//add all south edge tiles to array list
				for(int i = 0; i < GameSettings.GetCols()-1; i++) {
					playerGoalTiles.add(tiles[GameSettings.GetRows()-1][i]);
				}				
				break;
				
			case "west":
				//add all west edge tiles to array list
				for(int i = 0; i < GameSettings.GetRows()-1; i++) {
					playerGoalTiles.add(tiles[i][0]);
				}				
				break;		
			default:
				System.out.println("CheckAllPaths could not determine player goal!");
			}
			
			//we now have a list of goal tiles that this player must be able to reach.
			//check visitedTiles to see if ANY of the goal tiles were visited.
			Boolean canReachGoal = false;
			for(GameTile goalTile : playerGoalTiles) {
				if(visitedTiles[goalTile.GetXCoord()][goalTile.GetYCoord()] == true) {
					canReachGoal = true;
				}
			}
			
			//if this player can reach at least one goal tile, this temporary wall is permissible.
			if(canReachGoal) {
				pathIsBlocked = false;
			}
			else {
				System.out.println("Placing this wall would block " +thisPlayer.GetName()+ ".  Cannot place wall here!.");
				pathIsBlocked = true;
				//quit immediately
				return pathIsBlocked;
			}			
		}
		
		//after looping/checking all players and paths, return whether a player's path was blocked.
		return pathIsBlocked;
	}
	
	//recursive method	
	private static void CheckAllPaths(int rowAdd, int colAdd, JPanel[] tempWalls) {
		
		//base case
		//check if current tile has been visited. If already visited, stop recursion.
		if(visitedTiles[rowAdd][colAdd] == true) {
			return;
		}
		
		//if here, this is an unvisited location.  mark as visited.
		visitedTiles[rowAdd][colAdd] = true;
		
		//get this tile
		GameTile thisTile = tiles[rowAdd][colAdd];
		
		//if this tile has a neighbour in a particular direction N/S/E/W...
		//...and this tile has no existing wall in that particular direction...
		//...and the tempWalls requested do not contain a wall in that particular direction...
		//..recurse from that direction.
		
		//recurse north
		if(thisTile.HasNorthTile() && !thisTile.HasNorthWall()) {
			//check also if the tempWall would block north direction
			Boolean foundWallInTemp = false;
			for(int i = 0; i < tempWalls.length; i++) {
				if(thisTile.GetTop() == tempWalls[i]) {
					//north temp wall was found
					foundWallInTemp = true;
				}
			}
			//if we did not find the north panel among temp either, recurse north
			if(!foundWallInTemp) {
				CheckAllPaths((rowAdd-1), colAdd, tempWalls);
			}
		}
		
		//recurse east
		if(thisTile.HasEastTile() && !thisTile.HasEastWall()) {
			//check also if the tempWall would block east direction
			Boolean foundWallInTemp = false;
			for(int i = 0; i < tempWalls.length; i++) {
				if(thisTile.GetRight() == tempWalls[i]) {
					//east temp wall was found
					foundWallInTemp = true;
				}
			}
			//if we did not find the east panel among temp either, recurse east
			if(!foundWallInTemp) {
				CheckAllPaths(rowAdd, (colAdd+1), tempWalls);
			}
		}
		
		//recurse south
		if(thisTile.HasSouthTile() && !thisTile.HasSouthWall()) {
			//check also if the tempWall would block south direction
			Boolean foundWallInTemp = false;
			for(int i = 0; i < tempWalls.length; i++) {
				if(thisTile.GetBottom() == tempWalls[i]) {
					//south temp wall was found
					foundWallInTemp = true;
				}
			}
			//if we did not find the south panel among temp either, recurse south
			if(!foundWallInTemp) {
				CheckAllPaths((rowAdd+1), colAdd, tempWalls);
			}
		}
		
		//recurse west
		if(thisTile.HasWestTile() && !thisTile.HasWestWall()) {
			//check also if the tempWall would block west direction
			Boolean foundWallInTemp = false;
			for(int i = 0; i < tempWalls.length; i++) {
				if(thisTile.GetLeft() == tempWalls[i]) {
					//west temp wall was found
					foundWallInTemp = true;
				}
			}
			//if we did not find the west panel among temp either, recurse west
			if(!foundWallInTemp) {
				CheckAllPaths(rowAdd, (colAdd-1), tempWalls);
			}
		}
	}	
}

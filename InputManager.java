import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JPanel;

/*
 *  Input Manager Class implements serializable necessary for reading and writing save/load file
 *	
 *  currently top level class.  probably demote this when refactoring.
 * 	this class currently spawns the game board, which in turn spawns tiles.  move this! 
 *	this class need only handle incoming user input, and respond accordingly.  	
 */
public class InputManager implements Serializable {

	// store local ref to rows/cols here
	// Input manager uses them a lot
	private int rows;
	private int cols;

	// store local ref to gridtiles here. better way?
	// use setter below when ready to update.
	private GameTile[][] gridTiles;

	// collection of temporary (during mouseover) walls
	private JPanel[] tempWalls;

	// collection of all walls currently placed/locked
	private ArrayList<JPanel> allLockedWalls;

	// constructor
	public InputManager() {

		// input manager sees rows/cols of grid as 0-indexed!
		rows = GameSettings.GetRows() - 1;
		cols = GameSettings.GetCols() - 1;

		tempWalls = new JPanel[8];

		allLockedWalls = new ArrayList<JPanel>();

	}
	
	//Getter
	public ArrayList<JPanel> getAllLockedWalls() {
		return allLockedWalls;
	}

	// Setter
	public void SetGridTiles(GameTile[][] tiles) {
		gridTiles = tiles;
	}
	
	public void AddLockedWalls(ArrayList<JPanel> wallsToAdd) {
		//if the panel is not already on the list, lock it.
		for(JPanel panel : wallsToAdd) {
			if(!allLockedWalls.contains(panel)) {
				allLockedWalls.add(panel);
				//System.out.println("AI is Adding " +wallToAdd.getName());
			}
		}
	}

	private void DisplayTemporaryWalls() {

		// check to see if ANY of the panels listed in the tempWalls array are locked
		// if any are locked, do not display this entire set

		// set the wall color of each panel in the tempWall[] to display it
		for (JPanel thisPanel : tempWalls) {
			if (thisPanel != null) {
				// find the parent (the GameTile obj) that is responsible for this panel
				GameTile parent = (GameTile) thisPanel.getParent();
				// parent activates the wall (changes it's color to wall)
				parent.ActivateWallPanel(thisPanel);							
				// repeat until 8 walls have been activated.
			}
		}
	}

	public void ClearTemporaryWalls() {
		// set the bkg color of each panel in the tempWall[] to remove it from display,
		// and from list.
		for (JPanel thisPanel : tempWalls) {
			if (thisPanel != null) {
				// find the parent (the GameTile obj) that is responsible for this panel
				GameTile parent = (GameTile) thisPanel.getParent();
				// parent deactivates the wall (changes it's color to bkg)
				parent.DeactivateWallPanel(thisPanel);
				// repeat until 8 walls have been removed.
			}
		}

		// clear the array
		for (int i = 0; i < tempWalls.length; i++) {
			tempWalls[i] = null;
		}
	}

	// method to track mouse movement during gameplay.
	// this method tracks the mouse while it's moving (hovering) over the game
	// board.
	public void TrackMouseMovement(JPanel thisPanel, Point point) {
		
		// messages sent here from individual GameTiles.
		// each GameTile has 9 sub-panels (center tile and walls)
		// this method receives the sub-panel that detected the mouse, and the x,y point
		// of the mouse
		// track mouse position within the tile.
		
		//if game is paused (ie: showing a message panel), no action here
		if(GameSettings.GetGameIsPaused()) {
			return;
		}

		// get the name of the sub panel passed as param.
		String panelName = thisPanel.getName();
		// get the size of thisPanel
		Dimension panelSize = thisPanel.getSize();
		int panelWidth = (int) panelSize.getWidth();
		int panelHeight = (int) panelSize.getHeight();

		// get the parent of this sub panel (the GameTile that holds all 9 sub panels)
		GameTile parent;		
		if(panelName.equals("centerPanel")) {
			parent = (GameTile) thisPanel.getParent().getParent();			
		}
		else {			
			parent = (GameTile) thisPanel.getParent();			
		}

		// is mouse on a corner panel?
		// if so, ignore mouseover. do not display temporary wall placement when mouse
		// is at a corner.
		if (panelName.equals("topLeftWall") || panelName.equals("topRightWall") || panelName.equals("bottomLeftWall")
				|| panelName.equals("bottomRightWall")) {
			return;
		}

		// is mouse over the center tile panel? if so, highlight the center panel		
		if (panelName.equals("centerPanel")) {
			HandleMouseOverInput(parent, "c");
			return;
		}
		
		//next check to see if the current player has any walls left in inventory.
		//if no walls left, no need to check wall panels.
		if(GameController.GetCurrentPlayer().GetWallsRemaining() <= 0) {
			return;
		}

		// is user trying to place a wall on the left side of a tile?
		// if so, is it upper-left or lower left?
		if (panelName.equals("leftWall")) {
			// mouse is on the left
			// is mouse on upper, or lower, left side?
			if (point.y < panelHeight / 2) {
				// mouse is on LEFT side of tile, in the UPPER region of left sub panel.
				HandleMouseOverInput(parent, "lu");
			} else {
				// mouse is on LEFT side of tile, in the LOWER region of left sub panel.
				HandleMouseOverInput(parent, "ll");
			}
			return;
		}

		// is user trying to place a wall on the top side of a tile?
		// if so, is it top-left or top-right?
		if (panelName.equals("topWall")) {
			// mouse is on the top
			// is mouse on left, or right, of the top sub panel?
			if (point.x < panelWidth / 2) {
				// mouse is on TOP side of tile, in the LEFT region of top sub panel.
				HandleMouseOverInput(parent, "tl");
			} else {
				// mouse is on TOP side of tile, in the RIGHT region of top sub panel.
				HandleMouseOverInput(parent, "tr");
			}
			return;
		}

		// is user trying to place a wall on the right side of a tile?
		// if so, is it upper-right or lower right?
		if (panelName.equals("rightWall")) {
			// mouse is on the right
			// is mouse on upper, or lower, right side?
			if (point.y < panelHeight / 2) {
				// mouse is on RIGHT side of tile, in the UPPER region of right sub panel.
				HandleMouseOverInput(parent, "ru");
			} else {
				// mouse is on RIGHT side of tile, in the LOWER region of right sub panel.
				HandleMouseOverInput(parent, "rl");
			}
			return;
		}

		// is user trying to place a wall on the bottom side of a tile?
		// if so, is it bottom-left or bottom-right?
		if (panelName.equals("bottomWall")) {
			// mouse is on the bottom
			// is mouse on left, or right, of the bottom sub panel?
			if (point.x < panelWidth / 2) {
				// mouse is on BOTTOM side of tile, in the LEFT region of top sub panel.
				HandleMouseOverInput(parent, "bl");
			} else {
				// mouse is on BOTTOM side of tile, in the RIGHT region of top sub panel.
				HandleMouseOverInput(parent, "br");
			}
			return;
		}
	}

	/*
	 * //Params: parent = the GameTile (parent) object which contains the subpanel
	 * that detected mouse movement locationCode is where on the sub panel the mouse
	 * currently resides (the border panel, and position bias)
	 * 
	 * locationCode's: lu = left side, upper or ll = left side, lower tl = top side,
	 * left or tr = top side, right ru = right side, upper or rl = right side, lower
	 * bl = bottom side, left or br = bottom side, right c = center tile.
	 */
	public void HandleMouseOverInput(GameTile parent, String locationCode) {

		// Strategy:
		// gather the necessary walls for temporary display (mouseover shows temporary
		// walls, they are locked in when user clicks)
		// there will always be 8 wall borders or 8 null in the tempWallDisplay
		// access to four tiles is necessary in a 2-tile-spanning wall.
		// the 8 wall borders are collected from the 4 tiles involved.

		// get the coordinates of the tile mouse is currently on
		// used for checking if neighbours exist
		int x = parent.GetXCoord();
		int y = parent.GetYCoord();

		// clear temp walls
		ClearTemporaryWalls();

		// activate temporary (mouseover) walls, based on switch and location code
		switch (locationCode) {

		case "c":			
			
			//do anything else here?
			
			// clear any temporary walls
			ClearTemporaryWalls();
			return; // <-------Note: returning here. no execution of code after switch!

		case "tl":
			// if no north neighbour, or no west neighbour, return
			if (x == 0 || y == 0) {
				ClearTemporaryWalls();
				return;
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

		case "lu":
			// if no north neighbour, return
			if (x == 0) {
				return;
			}
			// if no west neighbour, return.
			if (y == 0) {
				return;
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

		case "tr":
			// if no north neighbour, return
			if (x == 0) {
				return;
			}
			// if no east neighbour, return.
			if (y == cols) {
				return;
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

		case "ru":
			// if no north neighbour, return
			if (x == 0) {
				return;
			}
			// if no east neighbour, return.
			if (y == cols) {
				return;
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

		case "bl":
			// if no south neighbour, return
			if (x == rows) {
				return;
			}
			// if no west neighbour, return.
			if (y == 0) {
				return;
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

		case "ll":
			// if no south neighbour, return
			if (x == rows) {
				return;
			}
			// if no west neighbour, return.
			if (y == 0) {
				return;
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

		case "br":
			// if no south neighbour, return
			if (x == rows) {
				return;
			}
			// if no east neighbour, return.
			if (y == cols) {
				return;
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

		case "rl":
			// if no south neighbour, return
			if (x == rows) {
				return;
			}
			// if no east neighbour, return.
			if (y == cols) {
				return;
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
			ClearTemporaryWalls();
			System.out.println("no match for locationCode");
		}

		// we now have a set of temporary walls to display.
		// if any of them are locked, discard the set and do nothing
		for (JPanel panelToCheck : tempWalls) {
			if (allLockedWalls.contains(panelToCheck)) {
				ClearTemporaryWalls();
				return;
			}
		}

		// CAN ADD OTHER CHECKS FOR WALL PLACEMENT HERE! IE: IF IT COMPLETELY BLOCKS
		// PLAYER...

		// otherwise, this list is valid. display it.
		DisplayTemporaryWalls();
	}

	public void HandleMouseClick(JPanel thisPanel) {
		// user clicked the mouse on thisPanel
		// what do they want to do?
		//if game is paused (ie: showing a message panel), no action here
		if(GameSettings.GetGameIsPaused()) {
			return;
		}		
				
		Player thisPlayer = GameController.GetCurrentPlayer();

		// trying to move the player?
		if (thisPanel.getName().equals("centerPanel")) {
			
			//is this tile available for move?			
			GameTile tile = (GameTile)thisPanel.getParent().getParent();			
			
			if(tile.CanAcceptPlayer()) {
				// player clicked on a valid center tile, and will move to it				
				//remove them from the tile they are standing on
				thisPlayer.GetTile().RemovePlayer();
				//add them to the tile that was clicked on (in param)
				tile.AddPlayer(thisPlayer);
				//update the player's location attribute to store new location.
				thisPlayer.setTile(tile);				
				//end the turn and advance to the next turn.
				GameSettings.GetGameController().AdvanceToNextTurn();				
			}			
			return;
		}

		// if user didn't click on a tile, they clicked on a wall area
		// tempWalls should always contain 8 wall borders, or 8 null's.
		// check if temp walls are empty (none to process). If (any/either) empty, no
		// action.
		for (JPanel tempPanel : tempWalls) {
			if (tempPanel == null) {
				return;
			}
		}
		
		//check player's inventory.  if they have used all their walls, no action
		if(thisPlayer.GetWallsRemaining() <= 0) {
			//System.out.println("Sorry, " +thisPlayer.GetName()+ ", you have no walls remaining.");
			return;
		}

		// else, not clicking on center, and we have temp walls from mouseover. 
		//check if this wall would block any players from reaching the goal
		if(CheckPath.ThisWallBlocksGoal(tempWalls)) {
			//System.out.println("This wall would block at least one player!  Cannot place wall here.");
			return;
		}		
		
		//if all conditions passed, place a wall

		// loop tempWalls and set each of it's panels to locked.
		// copy to a master list of allLockedWalls here.
		for (JPanel tempPanel : tempWalls) {
			// get the panel parent (the full GameTile)
			GameTile thisTile = (GameTile) tempPanel.getParent();
			// parent locks it's panel so it can no longer be changed.
			thisTile.LockWall(tempPanel);
			// add this panel to the list of all locked walls
			allLockedWalls.add(tempPanel);
		}
		// we have placed this wall. clear the tempWalls array.
		ClearTemporaryWalls();
		
		//subtract one from the player's wall inventory
		thisPlayer.setWallsRemaining(thisPlayer.GetWallsRemaining() - 1);
		//update playerUI display
		
		GameSettings.getInGameUIPanel().UpdatePlayerInfoDisplay();
		//System.out.println(thisPlayer.GetName() + " placed a wall.  They have " +thisPlayer.GetWallsRemaining()+ " walls remining.");
		//end the turn and advance to the next turn.
		GameSettings.GetGameController().AdvanceToNextTurn();	

	}
}

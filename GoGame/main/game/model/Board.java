package game.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.player.HumanPlayer;
import game.player.Player;

public class Board implements Cloneable, Serializable {

	private static final long serialVersionUID = 5015033032045544470L;
	private Intersection[][] intersections; //y, x
	public static final int DEFAULT_BOARD_SIZE = 9;
	
	private int boardSize;
	private Map<PlayerColor, Integer> stonesCaptured;
	
	private Set<StoneGroup> activeStoneGroups;
	private Map<PlayerColor, Integer> currentScore = null;

	public Board(Integer size){
		this.boardSize = size < 3 || size > DEFAULT_BOARD_SIZE ? DEFAULT_BOARD_SIZE : size;
		activeStoneGroups = new HashSet<>();
		intersections = new Intersection[size][size];
		for (int i = 0; i<size; i++){
			for (int j=0; j<size; j++){
				intersections[i][j] = new Intersection(j, i);
			}
		}
		
		for (int i = 0; i<size; i++){
			for (int j=0; j<size; j++){
				if (i>0) intersections[i][j].addNeighbor(intersections[i-1][j]);
				if (i<size-1) intersections[i][j].addNeighbor(intersections[i+1][j]);
				if (j>0) intersections[i][j].addNeighbor(intersections[i][j-1]);
				if (j<size-1) intersections[i][j].addNeighbor(intersections[i][j+1]);
			}
		}
		
		stonesCaptured = new HashMap<>();
		stonesCaptured.put(PlayerColor.BLACK, 0);
		stonesCaptured.put(PlayerColor.WHITE, 0);
	}
	
	/**
	 * @param move - the request move
	 * @param player - the player who requested the move
	 * @return - a set of stones that were captured as a result of this move
	 * @throws MoveException
	 */
	public Set<Stone> makeMove(Move move, PlayerColor player) throws MoveException{
		if (isLegalMove(move, player)){
			addStone(move.getX(), move.getY(), player);
			Set<Stone> captureStones = doCaptures(move.getX(), move.getY(), player);
			currentScore = null;
			return captureStones;
		} else {
			throw new MoveException("Invalid move");
		}
	
	}

	private Set<Stone> doCaptures(int x, int y, PlayerColor player) {
		Set<StoneGroup> toBeCaptured = new HashSet<>();
		for (Intersection neighbor : intersections[y][x].getNeighbors()){
			if (neighbor.isOccupied()){
				boolean isOtherPlayer = player != neighbor.getOccupant().getOwner();
				StoneGroup neighborStoneGroup = neighbor.getOccupant().getGroup();
				if (isOtherPlayer && neighborStoneGroup.getRemainingLiberties() == 1){
					toBeCaptured.add(neighborStoneGroup);
				}
			}
		}
		
		return doCaptureGroups(player, toBeCaptured);
	}

	private Set<Stone> doCaptureGroups(PlayerColor player,
			Set<StoneGroup> toBeCaptured) {
		Set<Stone> capturedStones = new HashSet<Stone>();
		for (StoneGroup group : toBeCaptured){
			Set<Stone> stones = group.getStones();
			for (Stone stone : stones){
				intersections[stone.y_location][stone.x_location].setOccupant(null);
				stone.setGroup(null);
			}
			capturedStones.addAll(stones);
			activeStoneGroups.remove(group);
		}
		
		stonesCaptured.put(player, stonesCaptured.get(player) + capturedStones.size());
		
		//obviously inefficient, make this faster eventually
		recalculateAllLiberties();
		return capturedStones;
	}

	private void recalculateAllLiberties() {
		Set<StoneGroup> activeStoneGroups = getActiveStoneGroups();
		for (StoneGroup group : activeStoneGroups){
			Set<Intersection> liberties = new HashSet<>();
			for (Stone stone : group.getStones()){
				for (Intersection neighbor: intersections[stone.y_location][stone.x_location].getNeighbors()){
					if (neighbor.getOccupant() == null){
						liberties.add(neighbor);
					}
				}
			}
			group.setRemainingLiberties(liberties.size());
		}
	}

	private Set<StoneGroup> getActiveStoneGroups() {
		return activeStoneGroups;
	}

	private void addStone(int x, int y, PlayerColor player) {
		Stone newStone = new Stone(x,y,player);
		intersections[y][x].setOccupant(newStone);
		boolean newStoneIsUnconnected = true;
		
		Set<StoneGroup> oldGroups = new HashSet<>();
		oldGroups.add(newStone.getGroup());
		for (Intersection i : intersections[y][x].getNeighbors()){
			if (i.isOccupied() && i.getOccupant().getOwner().equals(player)){
				oldGroups.add(i.getOccupant().getGroup());
				newStone.connect(i.getOccupant());
				newStoneIsUnconnected = false;
			}
		}
		
		oldGroups.remove(newStone.getGroup());
		activeStoneGroups.removeAll(oldGroups);
		
		if (newStoneIsUnconnected){
			activeStoneGroups.add(newStone.getGroup());
		}
		
	}

	public boolean isLegalMove(Move move, PlayerColor player) {
		int x = move.getX();
		int y = move.getY();
		boolean legalLocation = x >= 0 && x < boardSize && y >=0 && y < boardSize && intersections[y][x].getOccupant() == null;
		boolean isSelfCapture = true;
		if (legalLocation){
			for (Intersection neighbor : intersections[y][x].getNeighbors()){
				if (!neighbor.isOccupied()){
					isSelfCapture = false;
					break;
				} 
				boolean isNotSelfCapture = neighbor.getOccupant().getOwner().equals(player) && 
						neighbor.getOccupant().getGroup().getRemainingLiberties() > 1;
				boolean enemyIsCapturedFirst = !neighbor.getOccupant().getOwner().equals(player) && 
						neighbor.getOccupant().getGroup().getRemainingLiberties() == 1;
				if ( isNotSelfCapture || enemyIsCapturedFirst ){
						isSelfCapture = false;
						break;
				}
			}
		}
		return legalLocation && !isSelfCapture;
	}
	
	public static Board deserialize(String s) throws IOException, MoveException {
		String[] lines = s.split("\n");
		for (String line : lines){
			if (line.length() != lines.length){
				throw new IOException("Number of rows must equal number of columns");
			}
		}
		Player black = new HumanPlayer(PlayerColor.BLACK);
		Player white = new HumanPlayer(PlayerColor.WHITE);
		Board board = new Board(lines.length);
		for (int i = 0; i<lines.length; i++){
			for (int j = 0; j<lines.length; j++){
				if (lines[i].charAt(j) == 'B'){
					board.makeMove(Move.getMoveInstance(MoveType.NORMAL, j, i), black.getColor());
				} else if (lines[i].charAt(j) == 'W'){
					board.makeMove(Move.getMoveInstance(MoveType.NORMAL, j, i), white.getColor());
				}
			}
		}
		
		return board;
	}
	
	public boolean allTerritoryIsEnclosed() {
		boolean[][] visited = new boolean[boardSize][boardSize];
		Stack<Intersection> territoryStack = new Stack<>();
		for (StoneGroup group : activeStoneGroups){
			for (Stone stone : group.getStones()){
				for (Intersection neighbor : intersections[stone.y_location][stone.x_location].getNeighbors()){
					if (!neighbor.isOccupied() && !visited[neighbor.y_location][neighbor.x_location]){
						territoryStack.push(neighbor);
						if (0 == territoryEnclosed(territoryStack, visited, stone.getOwner())){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public Map<PlayerColor, Integer> getScore() {
		
		if (currentScore != null){
			return currentScore;
		}
		
		boolean[][] visited = new boolean[boardSize][boardSize];
		Stack<Intersection> territoryStack = new Stack<>();
		currentScore = new HashMap<>();
		currentScore.put(PlayerColor.WHITE, 0);
		currentScore.put(PlayerColor.BLACK, 0);
		for (StoneGroup group : activeStoneGroups){
			for (Stone stone : group.getStones()){
				for (Intersection neighbor : intersections[stone.y_location][stone.x_location].getNeighbors()){
					if (!neighbor.isOccupied() && !visited[neighbor.y_location][neighbor.x_location]){
						territoryStack.push(neighbor);
						currentScore.put( stone.getOwner(), currentScore.get(stone.getOwner()) + 
								territoryEnclosed(territoryStack, visited, stone.getOwner()));
					}
				}
			}
		}
		currentScore.put(PlayerColor.WHITE, currentScore.get(PlayerColor.WHITE) - stonesCaptured.get(PlayerColor.BLACK));
		currentScore.put(PlayerColor.BLACK, currentScore.get(PlayerColor.BLACK) - stonesCaptured.get(PlayerColor.WHITE));
		return currentScore;
	}
	
	
	public int getBoardSize() {
		return boardSize;
	}

	//assumes stack is size one with an unoccupied intersection
	private int territoryEnclosed(Stack<Intersection> territoryStack, boolean[][] visited, PlayerColor expectedColor){
		int territory = 1;
		while (!territoryStack.isEmpty()){
			Intersection i = territoryStack.pop();
			visited[i.y_location][i.x_location] = true;
			for (Intersection neighbor : i.getNeighbors()){
				if (neighbor.isOccupied() && !neighbor.getOccupant().getOwner().equals(expectedColor)){
					return 0;
				} else if (!neighbor.isOccupied() && !visited[neighbor.y_location][neighbor.x_location]){
					territory++;
					visited[neighbor.y_location][neighbor.x_location] = true;
					territoryStack.push(neighbor);
				}
			}
		}
		return territory;
	}

	@Override
	public String toString() {
		StringBuilder boardString = new StringBuilder();
		for (int i = 0; i<boardSize; i++){
			boardString.append(" " + i);
		}
		boardString.append("\n");
		String[] rows = this.serialize().split("\n");
		int i=0;
		for (String line : rows){
			StringBuilder withSpaces = new StringBuilder();
			for (char c : line.toCharArray()){
				withSpaces.append(c == ' ' ? c : " " + c);
			}
			boardString.append(withSpaces.toString() + " " + i + "\n");
			i++;
		}
		
		return "Board [board looks like:\n" + boardString.toString()
				+ "boardSize=" + boardSize + ", captured=" + stonesCaptured
				+ ", activeStoneGroups=" + activeStoneGroups + "]";
	}
	
	public int getStonesCaptures(PlayerColor playerColor){
		return stonesCaptured.get(playerColor);
	}
	
	public String serialize() {
		StringBuilder boardString = new StringBuilder();
		String end = "";
		for (int i = 0; i<boardSize; i++){
			StringBuilder line = new StringBuilder();
			for (int j = 0; j<boardSize; j++){
				if (intersections[i][j].isOccupied()){
					line.append(intersections[i][j].getOccupant().getOwner().equals(PlayerColor.BLACK) ? "B" : "W");
				} else {
					line.append("+");
				}
			}
			boardString.append(end + line.toString());
			end = "\n";
		}
		return boardString.toString();
	}

	@Override
	public Board clone(){
		String s = this.serialize();
		try {
			return Board.deserialize(s);
		} catch (IOException | MoveException e) {
			return null;
		}
	}

	public void captureDeadGroups() {
		//eventually do Bensen's but for now, be stupid and delete 
		//anything that only has a single liberty, assume this will
		//only be called when there are no more valid moves
		Set<StoneGroup> blackToBeCaptured = new HashSet<>();
		Set<StoneGroup> whiteToBeCaptured = new HashSet<>();
		for (StoneGroup group : activeStoneGroups){
			if (group.getRemainingLiberties() == 1){
				if (group.getOwner().equals(PlayerColor.WHITE)){
					whiteToBeCaptured.add(group);
				} else {
					blackToBeCaptured.add(group);
				}
			}
		}
		
		doCaptureGroups(PlayerColor.BLACK, blackToBeCaptured);
		doCaptureGroups(PlayerColor.WHITE, whiteToBeCaptured);
	}
	
	//TODO: needed for ko by GameState, is there a better way?
	public Integer getStoneGroupLibertiesAtLocation(Integer x, Integer y){
		return intersections[y][x].getOccupant().getGroup().getRemainingLiberties();
	}
	
}

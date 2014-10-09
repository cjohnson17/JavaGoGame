package app;
import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.model.Board;
import game.model.Stone;
import game.model.Move;
import game.model.StoneGroup;
import game.player.Player;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameState implements Cloneable {
	
	private int boardSize;
	private int turn;
	
	private LinkedList<Player> players;
	private Deque<MoveResult> moveHistory;
	private Board board;
	
	public GameState(){
		this(AppRunner.BOARD_SIZE, new ArrayList<Player>());
	}
	
	/**
	 * Creates an empty game state
	 * @param boardSize		width / length of the board that will be created
	 * @param players		players who are playing the game. The player at players[0] will go first
	 */
	public GameState(int boardSize, List<Player> players){
		moveHistory = new LinkedList<>();
		board = new Board(boardSize);
		this.players = new LinkedList<Player>();
		this.players.addAll(players);
		turn = 0;
	}
	
	/**
 	* Returns a clone of the current game state
 	*/
	public GameState clone() {
		GameState state = new GameState(boardSize, players);
		state.board = this.board.clone();
		for (Iterator<MoveResult> it = this.moveHistory.iterator(); it.hasNext();){
			state.moveHistory.add(it.next());
		}
		return state;
	}
	
	/**
	 * Attempts to play a move for the player who is next
	 * @param move				
	 * @throws MoveException	throws if move is invalid for this players
	 */
	public void addMove(Move move) throws MoveException{
		Player currentPlayer = players.poll();
		Set<Stone> captured;
		if (move.getType().equals(MoveType.PASS)){
			captured = new HashSet<>();
		} else {
			captured = board.makeMove(move, currentPlayer.getColor());
		}
		players.add(currentPlayer);
		moveHistory.push(new MoveResult(move, captured, ++turn));
	}
	
	/**
	 * Returns a set of moves for the requested player
	 * @param player 	the requested player
	 * @return			the set of possible moves
	 */
	public Set<Move> getPossibleMoves(PlayerColor player) {
		Set<Move> moves = new HashSet<Move>();
		for (int i=0; i<board.getBoardSize(); i++){
			for (int j=0; j<board.getBoardSize(); j++){
				Move move = Move.getMoveInstance(MoveType.NORMAL, i, j);
				if (isLegalMove(move, player)){
					moves.add(move);
				}
			}
		}
		return moves;
	}
	
	/**
	 * @return	the last move that was played
	 */
	public Move getLastMove(){
		return getLastMove(getLastMoved().getColor());
	}
	
	/**
	 * Returns the last move that was played by a player
	 * @param color		the color of the requested player
	 * @return			the last move that was played
	 */
	public Move getLastMove(PlayerColor color){
		Move move = null;
		if (!moveHistory.isEmpty()){
			move = moveHistory.peekFirst().move;
			if (players.peekLast().getColor().equals(color)){
				return move;
			} else {
				move = null;
				MoveResult temp = moveHistory.poll();
				if (!moveHistory.isEmpty()){
					move = moveHistory.peekFirst().move;
				}
				moveHistory.push(temp);
			}
		}
		return move;
	}
	
	/**
	 * Returns whether or not the move is valid, includes ko logic
	 * @param move		the move in question
	 * @param color		the player who is playing the move
	 * @return			true if move is valid, false otherwise
	 */
	public boolean isLegalMove(Move move, PlayerColor color){
		if (move == null){
			return false;
		} else if (move.equals(Move.getMoveInstance(MoveType.PASS, 0, 0))){
			return true;
		}
		Boolean boardLegal = board.isLegalMove(move, color);
		MoveResult last = moveHistory.peekFirst();
		Boolean retakingKo = last != null && last.captured.size() == 1 && 
				board.getStoneGroupLibertiesAtLocation(last.move.getX(), last.move.getY()) == 1 &&
				move.getX().equals(last.captured.iterator().next().x_location) &&
				move.getY().equals(last.captured.iterator().next().y_location);
		
		return boardLegal && !retakingKo;
	}
	
	/**
	 * Returns whether the location defined by the move is an eye for the requested player
	 * @param move		the move representing the x,y location of the eye
	 * @param color		the player who "owns" the eye
	 * @return			returns true if this is an eye for this player, false otherwise
	 */
	public boolean isEye(Move move, PlayerColor color){
		return board.isEye(move, color);
	}

	/**
	 * @return	get the last player moved
	 */
	public Player getLastMoved() {
		return players.peekLast();
	}
	
	/**
	 * @return	get the player whose turn it is
	 */
	public Player getNextToMove() {
		return players.peek();
	}
	
	/**
	 * @return	returns the score
	 */
	public Map<PlayerColor, Integer> getScore(){
		return board.getScore();
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder("MoveHistory: " + moveHistory.toString());
		b.append(board.toString());
		return b.toString();
	}
	
	/**
	 *	Used to store information 
	 */
	private class MoveResult {
		public Move move;
		public Set<Stone> captured;
		public int turn;
		
		public MoveResult(Move move, Set<Stone> captured, int turn) {
			this.move = move;
			this.captured = captured;
			this.turn = turn;
		}

		@Override
		public String toString() {
			return "MoveResult [move=" + move + ", captured=" + captured
					+ ", turn=" + turn + "]";
		}

	}


	public void captureDeadGroups() {
		//do Bensen's algorithm in the future
		board.captureDeadGroups();
	}

	public Set<StoneGroup> getGroupsInAtari() {
		return board.getGroupsInAtari();
	}

	public StoneGroup getStoneGroupAt(Move move) {
		return board.getStoneGroupAt(move);
	}
}

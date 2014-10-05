package app;
import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.model.Board;
import game.model.Stone;
import game.model.Move;
import game.player.Player;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class GameState implements Cloneable {
	
	private static final int boardSize = 6;
	
	private LinkedList<Player> players;
	private Deque<MoveResult> moveHistory;
	private Board board;
	
	public GameState(){
		this(new ArrayList<Player>());
	}
	
	public GameState(List<Player> players){
		moveHistory = new LinkedList<>();
		board = new Board(boardSize);
		this.players = new LinkedList<Player>();
		this.players.addAll(players);
	}
	
	public GameState clone() {
		GameState state = new GameState(players);
		state.board = this.board.clone();
		for (Iterator<MoveResult> it = this.moveHistory.iterator(); it.hasNext();){
			state.moveHistory.add(it.next());
		}
		return state;
	}
	
	public void addMove(Move move) throws MoveException{
		Player currentPlayer = players.poll();
		Set<Stone> captured;
		if (move.getType().equals(MoveType.PASS)){
			captured = new HashSet<>();
		} else {
			captured = board.makeMove(move, currentPlayer.getColor());
		}
		players.add(currentPlayer);
		moveHistory.push(new MoveResult(move, captured));
	}
	
	public List<Move> getPossibleMoves(PlayerColor player) {
		List<Move> moves = new ArrayList<Move>();
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
	
	public Move getLastMove(){
		Move move = null;
		if (!moveHistory.isEmpty()){
			move = moveHistory.peekFirst().move;
		}
		return move;
	}
	
	
	public boolean isLegalMove(Move move, PlayerColor color){
		Boolean boardLegal = board.isLegalMove(move, color);
		MoveResult last = moveHistory.peekFirst();
		Boolean retakingKo = last != null && last.captured.size() == 1 && 
				board.getStoneGroupLibertiesAtLocation(last.move.getX(), last.move.getY()) == 1 &&
				move.getX().equals(last.captured.iterator().next().x_location) &&
				move.getY().equals(last.captured.iterator().next().y_location);
		
		return boardLegal && !retakingKo;
	}

	public Player getLastMoved() {
		return players.peekLast();
	}
	
	public Player getNextToMove() {
		return players.peek();
	}
	
	public Map<PlayerColor, Integer> getScore(){
		return board.getScore();
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder("MoveHistory: " + moveHistory.toString());
		b.append(board.toString());
		return b.toString();
	}
	
	
	private class MoveResult {
		public Move move;
		public Set<Stone> captured;
		
		public MoveResult(Move move, Set<Stone> captured) {
			this.move = move;
			this.captured = captured;
		}
	}


	public void captureDeadGroups() {
		//do Bensen's algorithm
		board.captureDeadGroups();
	}

	public boolean isInKo() {
		// TODO Auto-generated method stub
		return false;
	}
}

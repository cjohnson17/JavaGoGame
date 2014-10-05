package game.player;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import app.GameState;
import enums.MoveType;
import enums.PlayerColor;
import game.model.Move;

public class MCTSNode {
	private Move move;
	private List<MCTSNode> children;
	private MCTSNode parent;
	private double wins;
	private double visits;
	private PlayerColor playerJustMoved;
	private List<Move> untriedMoves;
	
	private static final float UCTK = 1.0f;
	
	
	public MCTSNode(Move move, MCTSNode parent, GameState state){
		this.move = move;
		this.parent = parent;
		wins = 0;
		visits = 0;
		playerJustMoved = state.getLastMoved().getColor();
		untriedMoves = state.getPossibleMoves(PlayerColor.BLACK.equals(playerJustMoved) ? PlayerColor.WHITE : PlayerColor.BLACK);
		children = new LinkedList<>();
	}
	
	public MCTSNode UCTSelectChild() {
		MCTSNode selected = null;
		double best=Double.MIN_VALUE;
		for (MCTSNode node : children){
			if (node.getMove().getType().equals(MoveType.PASS) && selected == null){
				selected = node;
			} else {
				double uctValue = node.wins/node.visits + UCTK*Math.sqrt(2*Math.log(this.visits)/node.visits);
				if (uctValue > best){
					selected = node;
				}
			}
		}
		return selected;
	}
	
	public MCTSNode addChild(Move move, GameState state){
		untriedMoves.remove(move);
		MCTSNode node = new MCTSNode(move, this, state);
		children.add(node);
		return node;
	}
	
	public void update(Boolean wonResult){
		visits +=1;
		wins += wonResult ? 1 : 0;
	}

	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public List<MCTSNode> getChildren() {
		return children;
	}

	public void setChildren(List<MCTSNode> children) {
		this.children = children;
	}

	public MCTSNode getParent() {
		return parent;
	}

	public void setParent(MCTSNode parent) {
		this.parent = parent;
	}

	public double getWins() {
		return wins;
	}

	public void setWins(double wins) {
		this.wins = wins;
	}

	public Double getVisits() {
		return visits;
	}

	public void setVisits(double visits) {
		this.visits = visits;
	}

	public PlayerColor getPlayerJustMoved() {
		return playerJustMoved;
	}

	public void setPlayerJustMoved(PlayerColor playerJustMoved) {
		this.playerJustMoved = playerJustMoved;
	}

	public List<Move> getUntriedMoves() {
		return untriedMoves;
	}

}

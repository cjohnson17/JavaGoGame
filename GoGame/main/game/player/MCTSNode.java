package game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import app.AppRunner;
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
	private Set<Move> untriedMoves;
	
	private static final double epsilon = 1e-6;
	
	private static final Random random = new Random();
	
	
	public MCTSNode(Move move, MCTSNode parent, GameState state){
		this.move = move;
		this.parent = parent;
		wins = 0;
		visits = 0;
		playerJustMoved = state.getLastMoved().getColor();
		untriedMoves = state.getPossibleMoves(PlayerColor.BLACK.equals(playerJustMoved) ? PlayerColor.WHITE : PlayerColor.BLACK);
		children = new ArrayList<>();
	}
	
	/**
	 * Select the best child based on Upper Confidence Bound. This balances exploration (nodes not
	 * traveled very often) and exploitation (nodes that are known to have a high win ratio)
	 * @return The optimal node to playout
	 */
	public MCTSNode UCTSelectChild() {
		MCTSNode selected = null;
		double best = -1;
		for (MCTSNode node : children){
			if (node.getMove().getType().equals(MoveType.PASS) && selected == null){
				selected = node;
			} else {
				//disincentivize playing on the edges
				double ratio;
				if (node.getMove().getX()==0 || node.getMove().getY()==0 || 
						node.getMove().getX() == AppRunner.BOARD_SIZE-1 || 
						node.getMove().getY() == AppRunner.BOARD_SIZE-1){
					ratio = Math.max(0, (node.wins - 6.1)/(epsilon + node.visits));
				} else {
					ratio = node.wins/(epsilon + node.visits);
				}
				double V = Math.max(.001, ratio * (1-ratio));
				V = 1;
				double uctValue = ratio + Math.sqrt(V*Math.log(this.visits + 1)/(epsilon + node.visits)) + random.nextDouble()*epsilon;
				if (uctValue > best){
					selected = node;
					best = uctValue;
				}
			}
		}
		return selected;
	}
	
	/**
	 * Transfer the node representing the move [that was just played] from the untried bucket to the tried bucket
	 * @param 	move 	the move that was just played
	 * @param 	state 	the current state of the game (with move just played)
	 * @return 			the child node
	 */
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

	public Set<Move> getUntriedMoves() {
		return untriedMoves;
	}

	@Override
	public String toString() {
		return "MCTSNode [move=" + move + ", wins=" + wins + ", visits="
				+ visits + "]";
	}

}

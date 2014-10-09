package game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import app.GameState;
import game.model.Board;
import game.model.Intersection;
import game.model.Move;
import game.model.StoneGroup;

/**
 * Go Player using non-parallel Monte Carlo Tree Search with a default of 10k moves
 * @author calebj
 */
public class MCTSComputerPlayer extends Player {

	private static int ITERATIONS_PER_MOVE = 10000;
	private Random random;
	
	//Other person's move
	private MCTSNode root;
	
	public MCTSComputerPlayer(PlayerColor color) {
		super(color);
		random = new Random();
	}
	
	public MCTSComputerPlayer(PlayerColor color, Integer iterations){
		this(color);
		ITERATIONS_PER_MOVE = iterations;
	}

	@Override
	public Move getMove(GameState state) {
		
		if (state.getLastMoved().equals(color)){
			//we shouldn't have been called, it's not our turn
			return Move.getMoveInstance(MoveType.PASS, 0, 0);
		}
		
		Move lastMove = state.getLastMove();
		if (root != null && lastMove != null && !lastMove.getType().equals(MoveType.PASS)){
			pruneTree(lastMove, state);
		} else {
			root = new MCTSNode(null, null, state);
		}
		
		
		 try {
			Move move = UCT(state, ITERATIONS_PER_MOVE);
			pruneTree(move, state);
			return move;
		} catch (MoveException e) {
			return Move.getMoveInstance(MoveType.PASS, 0, 0);
		}
	}
	
	//performs basic MCTS with Upper Confidence Bound for Trees
	private Move UCT(GameState rootstate, int iterationsPerMove) throws MoveException {
		for (int i=0; i<iterationsPerMove; i++){
			MCTSNode node = root;
			GameState state = rootstate.clone();
			
			//Select
			while (node.getUntriedMoves().isEmpty() && !node.getChildren().isEmpty()){
				node = node.UCTSelectChild();
				state.addMove(node.getMove());
			}
			
			//Expand
			if (node.getUntriedMoves() != null){
				Set<Move> untried = node.getUntriedMoves();
				stripBadMoves(untried, state);
				Move m;
				if (untried.isEmpty()){
					m = Move.getMoveInstance(MoveType.PASS, 0, 0);
				} else {
					List<Move> possibleMoveList = new ArrayList<>(untried);
					m = possibleMoveList.get(random.nextInt(untried.size()));
				}
				state.addMove(m);
				node = node.addChild(m, state);
			}
			
			//Rollout
			Set<Move> possibleMoves = state.getPossibleMoves(state.getNextToMove().getColor());
			Map<PlayerColor, Boolean> justPassed = new HashMap<PlayerColor, Boolean>();
			justPassed.put(PlayerColor.BLACK, false);
			justPassed.put(PlayerColor.WHITE, false);
			while (!possibleMoves.isEmpty()){
				Move m = getBestMove(possibleMoves, state);
				if (m.equals(Move.getMoveInstance(MoveType.PASS, 0, 0))){
					if (!justPassed.get(state.getNextToMove().getColor())){
						justPassed.put(state.getNextToMove().getColor(), Boolean.TRUE);
					} else {
						break;
					}
				} else {
					justPassed.put(state.getNextToMove().getColor(), Boolean.FALSE);
				}
				state.addMove(m);
				possibleMoves = state.getPossibleMoves(state.getNextToMove().getColor());

			}

			state.captureDeadGroups();
				
			
			//Backpropogate
			while (node != null){
				Map<PlayerColor, Integer> score = state.getScore();
				Integer myScore = 0;
				Integer enemyScore = 0;
				for (Entry<PlayerColor, Integer> entry : score.entrySet()){
					if (entry.getKey().equals(node.getPlayerJustMoved())){
						myScore = entry.getValue();
					} else {
						enemyScore = entry.getValue();
					}
				}
				node.update(myScore > enemyScore || (myScore == enemyScore && node.getPlayerJustMoved().equals(PlayerColor.WHITE)));
				node = node.getParent();
			}
		}
		
		return Collections.max(root.getChildren(), new Comparator<MCTSNode>(){
			@Override
			public int compare(MCTSNode o1, MCTSNode o2) {
				return o1.getVisits().compareTo(o2.getVisits());
			}
		}).getMove();
	}

	private Move getBestMove(Set<Move> possibleMoves, GameState state) {
		/**
		 * This method determines the best move to try based on the following logic
		 * (some portions not yet implemented, marked as such (NYI):
		 
		 if the last move is an atari, then
		 	Save the stones which are in atari.
	(NYI)else if there is an empty location among the 8 locations around the last move which matches a pattern then
	(NYI)	Play randomly uniformly in one of these locations.
	(NYI)else if there is a move which captures stones then
			Capture stones.
		else if there is a legal move then
			Play randomly a legal move that doesn't kill your own eyespace
		else
			Return pass.
		end if
		 */
		Player currentPlayer = state.getNextToMove();
		stripBadMoves(possibleMoves, state);
		if (possibleMoves.isEmpty()){
			return Move.getMoveInstance(MoveType.PASS, 0, 0);
		}

		//save stones that are in atari
		Move move = state.getLastMove(currentPlayer.getColor());
		StoneGroup group = state.getStoneGroupAt(move);
		if (group != null && group.getRemainingLiberties()==1){
			Intersection i = group.getLiberties().iterator().next();
			move = Move.getMoveInstance(MoveType.NORMAL, i.x_location, i.y_location);
			if (possibleMoves.contains(move)){
				return move;
			}
		}
		
		//implement pattern matching here at a later time
		
		
		//try to kill enemy groups
		Set<StoneGroup> groups = state.getGroupsInAtari();
		Set<StoneGroup> enemyGroups = new TreeSet<>(new Comparator<StoneGroup>(){
			@Override
			public int compare(StoneGroup o1, StoneGroup o2) {
				Integer o2Size = o2.getStones().size();
				Integer o1Size = o1.getStones().size();
				return o2Size.compareTo(o1Size);
			}
		});
		for (StoneGroup atariGroup : groups){
			if (!atariGroup.getOwner().equals(currentPlayer.getColor())){
				enemyGroups.add(atariGroup);
			}
		}
		int asi = 0;
		for (StoneGroup enemyGroup : enemyGroups){
			Intersection i = enemyGroup.getLiberties().iterator().next();
			Move m = Move.getMoveInstance(MoveType.NORMAL, i.x_location, i.y_location);
			if (possibleMoves.contains(m)){
				return m;
			}
		}
		
		//play randomly
		List<Move> moveList = new ArrayList<>(possibleMoves);
		return moveList.get(random.nextInt(possibleMoves.size()));
	}

	
	//don't try moves that reduce your own eye space
	private void stripBadMoves(Set<Move> possibleMoves, GameState state) {
		for (Iterator<Move> it = possibleMoves.iterator(); it.hasNext();){
			Move move = it.next();
			if (state.isEye(move, state.getNextToMove().getColor())){
				it.remove();
			}
		}
	}

	//lets help the GC out
	private void pruneTree(Move lastMove, GameState state) {
		MCTSNode newNode = new MCTSNode(lastMove, null, state);
		for (MCTSNode node : root.getChildren()){
			if (node.getMove().equals(lastMove)){
				newNode = node;
			}
			node.setParent(null);
		}
		if (root != null){
			root.getChildren().clear();
		}
		root = newNode;
	}

}

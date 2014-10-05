package game.player;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import app.GameState;
import game.model.Board;
import game.model.Move;

/**
 * Go Player using Monte Carlo Tree Search. Even at 10k iterations, this player still
 * seems very weak (30k+). A small amount of domain specific knowledge would likely 
 * greatly increase the strength (i.e. trying to capture groups in atari, trying to escape if
 * it has groups in atari, not filling in eyes, etc) by at least 5-10k.
 * @author calebj
 *
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
		if (lastMove != null && !lastMove.getType().equals(MoveType.PASS)){
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
				List<Move> untried = node.getUntriedMoves();
				Move m;
				if (untried.isEmpty()){
					m = Move.getMoveInstance(MoveType.PASS, 0, 0);
				} else {
					m = untried.get(random.nextInt(untried.size()));
				}
				state.addMove(m);
				node = node.addChild(m, state);
			}
			
			//Rollout
			//TODO: this is going to be very slow, O(n^2) and lots of garbage, how can we make this faster? state.getrandom?
			List<Move> possibleMoves = state.getPossibleMoves(state.getNextToMove().getColor());
			Map<PlayerColor, Boolean> justPassed = new HashMap<PlayerColor, Boolean>();
			justPassed.put(PlayerColor.BLACK, false);
			justPassed.put(PlayerColor.WHITE, false);
			while (!possibleMoves.isEmpty()){
				state.addMove(possibleMoves.get(random.nextInt(possibleMoves.size())));
				possibleMoves = state.getPossibleMoves(state.getNextToMove().getColor());
				if (possibleMoves.isEmpty() && !justPassed.get(state.getNextToMove().getColor())){
					justPassed.put(state.getNextToMove().getColor(), Boolean.TRUE);
					possibleMoves.add(Move.getMoveInstance(MoveType.PASS, 0, 0));
				} else {
					justPassed.put(state.getNextToMove().getColor(), false);
				}
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
				return o2.getVisits().compareTo(o1.getVisits());
			}
		}).getMove();
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

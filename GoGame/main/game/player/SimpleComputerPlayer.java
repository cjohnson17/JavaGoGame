package game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.GameState;
import enums.MoveType;
import enums.PlayerColor;
import game.model.Board;
import game.model.Move;

public class SimpleComputerPlayer extends Player {
	
	List<Move> randomMoveList;

	public SimpleComputerPlayer(PlayerColor color) {
		super(color);
	}

	@Override
	public Move getMove(GameState state) {
		if (randomMoveList == null || randomMoveList.size() == 0){
			randomMoveList = new ArrayList(state.getPossibleMoves(color));
			Collections.shuffle(randomMoveList);
		}
		
		if (randomMoveList.size() == 0){
			return Move.getMoveInstance(MoveType.PASS, 0, 0);
		}
		Move next;
		do {
			if (randomMoveList.isEmpty()){
				return Move.getMoveInstance(MoveType.PASS, 0, 0);
			}
			next = randomMoveList.remove(0);
		} while (!state.isLegalMove(next, color));
		return next;	
	}

}

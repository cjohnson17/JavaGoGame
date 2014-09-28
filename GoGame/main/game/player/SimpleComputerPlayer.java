package game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public Move getMove(Board board) {
		if (randomMoveList == null){
			randomMoveList = new ArrayList<>(board.getBoardSize() * board.getBoardSize());
			for (int i = 0; i < board.getBoardSize(); i++){
				for (int j = 0; j < board.getBoardSize(); j++){
					randomMoveList.add(new Move(MoveType.NORMAL, i, j));
				}
			}
			Collections.shuffle(randomMoveList);
		}
		
		if (randomMoveList.size() == 0){
			return new Move(MoveType.PASS, null, null);
		}
		Move next = randomMoveList.remove(0);
		while (!board.isLegalMove(next, this.getColor())){
			next = randomMoveList.remove(0);
		}
		return next;	
	}

}

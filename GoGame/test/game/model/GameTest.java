package game.model;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import app.GameState;
import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.player.HumanPlayer;
import game.player.Player;

public class GameTest {

	
	@Test
	public void testKo() throws MoveException {
		List<Player> players = new ArrayList<>();
		players.add(new HumanPlayer(PlayerColor.BLACK));
		players.add(new HumanPlayer(PlayerColor.WHITE));
		GameState g = new GameState(players);

		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 1, 0));
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 2, 0));
		
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 0, 1));
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 3, 1));
		
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 1, 2));
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 2, 2));
		
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 2, 1));
		//capture
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 1, 1));
		
		assertFalse(g.isLegalMove(Move.getMoveInstance(MoveType.NORMAL, 2, 1), g.getNextToMove().getColor()));
	}
}

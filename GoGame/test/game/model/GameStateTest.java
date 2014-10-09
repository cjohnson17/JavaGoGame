package game.model;

import static org.junit.Assert.*;

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

public class GameStateTest {

	
	@Test
	public void testKo() throws MoveException {
		List<Player> players = new ArrayList<>();
		players.add(new HumanPlayer(PlayerColor.BLACK));
		players.add(new HumanPlayer(PlayerColor.WHITE));
		GameState g = new GameState(6, players);

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
	
	@Test
	public void testGetLastMoveMethods() throws MoveException {
		List<Player> players = new ArrayList<>();
		players.add(new HumanPlayer(PlayerColor.BLACK));
		players.add(new HumanPlayer(PlayerColor.WHITE));
		GameState g = new GameState(6, players);

		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 1, 0));
		assertEquals(PlayerColor.BLACK, g.getLastMoved().getColor());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 1, 0), g.getLastMove());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 1, 0), g.getLastMove(PlayerColor.BLACK));
		
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 2, 0));
		assertEquals(PlayerColor.WHITE, g.getLastMoved().getColor());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 2, 0), g.getLastMove());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 2, 0), g.getLastMove(PlayerColor.WHITE));
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 1, 0), g.getLastMove(PlayerColor.BLACK));
		
		g.addMove(Move.getMoveInstance(MoveType.NORMAL, 3, 0));
		assertEquals(PlayerColor.BLACK, g.getLastMoved().getColor());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 3, 0), g.getLastMove());
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 3, 0), g.getLastMove(PlayerColor.BLACK));
		assertEquals(Move.getMoveInstance(MoveType.NORMAL, 2, 0), g.getLastMove(PlayerColor.WHITE));
	}
}

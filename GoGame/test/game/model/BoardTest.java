package game.model;

import static org.junit.Assert.*;

import java.io.IOException;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.player.Player;
import game.player.HumanPlayer;

import org.junit.Test;

public class BoardTest {

	Player white = new HumanPlayer(PlayerColor.WHITE);
	Player black = new HumanPlayer(PlayerColor.BLACK);
	
	@Test
	public void testMakeMove() throws MoveException {
		Board b = new Board(3);
		Move move = new Move(MoveType.NORMAL, 0,0);
		assertTrue(b.isLegalMove(move, white.getColor()));
		b.makeMove(move, white.getColor());
		assertFalse(b.isLegalMove(move, white.getColor()));
	}
	
	@Test
	public void testSingleCapture() throws IOException, MoveException {
		String in = 
				"+W+\n" +
				"WBW\n" +
				"+++";
		
		Board b = Board.fromString(in);
		b.makeMove(new Move(MoveType.NORMAL, 1, 2), white.getColor());
		assertTrue(b.isLegalMove(new Move(MoveType.NORMAL, 1, 1), white.getColor()));
		assertEquals(new Integer(5), b.getScore().get(PlayerColor.WHITE));
		assertEquals(new Integer(-1), b.getScore().get(PlayerColor.BLACK));
	}
	
	@Test
	public void testMultiCatpure() throws IOException, MoveException {
		String in = 
				"+++++\n" +
				"WW+WW\n" +
				"BB+BB\n" +
				"WW+WW\n" +
				"+++++";
		Board b =  Board.fromString(in);
		b.makeMove(new Move(MoveType.NORMAL, 2, 2), black.getColor());
		b.makeMove(new Move(MoveType.NORMAL, 2, 1), white.getColor());
		assertEquals(new Integer(5), b.getScore().get(PlayerColor.WHITE));
		b.makeMove(new Move(MoveType.NORMAL, 2, 3), black.getColor());
		b.makeMove(new Move(MoveType.NORMAL, 2, 4), white.getColor());
		assertEquals(new Integer(15), b.getScore().get(PlayerColor.WHITE));
		assertEquals(new Integer(-6), b.getScore().get(PlayerColor.BLACK));
	}

}

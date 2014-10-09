package game.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Set;

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
		Move move = Move.getMoveInstance(MoveType.NORMAL, 0,0);
		assertTrue(b.isLegalMove(move, white.getColor()));
		b.makeMove(move, white.getColor());
		assertFalse(b.isLegalMove(move, white.getColor()));
	}
	
	@Test
	public void testSelfCaptureIsNotAllowed() throws MoveException, IOException {
		String in = 
				"+W+\n" +
				"W+W\n" +
				"+W+";
		Board b = Board.deserialize(in);
		Move move = Move.getMoveInstance(MoveType.NORMAL, 1,1);
		assertFalse(b.isLegalMove(move, black.getColor()));
	}
	
	@Test
	public void testSelfCaptureIsAllowedIfPlayerCapturesInTheProcess() throws MoveException, IOException {
		String in = 
				"B++\n" +
				"WB+\n" +
				"+WB";
		Board b = Board.deserialize(in);
		Move move = Move.getMoveInstance(MoveType.NORMAL, 0,2);
		assertTrue(b.isLegalMove(move, black.getColor()));
	}
	
	@Test
	public void testSingleCapture() throws IOException, MoveException {
		String in = 
				"+W+\n" +
				"WBW\n" +
				"+++";
		
		Board b = Board.deserialize(in);
		Set<Stone> captured = b.makeMove(Move.getMoveInstance(MoveType.NORMAL, 1, 2), white.getColor());
		assertTrue(b.isLegalMove(Move.getMoveInstance(MoveType.NORMAL, 1, 1), white.getColor()));
		assertEquals(new Integer(5), b.getScore().get(PlayerColor.WHITE));
		assertEquals(new Integer(-1), b.getScore().get(PlayerColor.BLACK));
		assertEquals(1, captured.size());
	}
	
	@Test
	public void testMultiCatpure() throws IOException, MoveException {
		String in = 
				"+++++\n" +
				"WW+WW\n" +
				"BB+BB\n" +
				"WW+WW\n" +
				"+++++";
		Board b =  Board.deserialize(in);
		b.makeMove(Move.getMoveInstance(MoveType.NORMAL, 2, 2), black.getColor());
		b.makeMove(Move.getMoveInstance(MoveType.NORMAL, 2, 1), white.getColor());
		assertEquals(new Integer(5), b.getScore().get(PlayerColor.WHITE));
		b.makeMove(Move.getMoveInstance(MoveType.NORMAL, 2, 3), black.getColor());
		Set<Stone> captured = b.makeMove(Move.getMoveInstance(MoveType.NORMAL, 2, 4), white.getColor());
		assertEquals(new Integer(15), b.getScore().get(PlayerColor.WHITE));
		assertEquals(new Integer(-6), b.getScore().get(PlayerColor.BLACK));
		assertEquals(6, captured.size());
	}
	
	@Test
	public void testIsEye() throws IOException, MoveException {
		String in = 
				"+W+W+\n" +
				"WW++W\n" +
				"+++++\n" +
				"BBB++\n" +
				"++B++";
		Board b =  Board.deserialize(in);
		assertTrue(b.isEye(Move.getMoveInstance(MoveType.PASS, 0, 0), white.getColor()));
		
		assertFalse(b.isEye(Move.getMoveInstance(MoveType.PASS, 4, 0), white.getColor()));
		assertFalse(b.isEye(Move.getMoveInstance(MoveType.PASS, 0, 4), black.getColor()));
		assertFalse(b.isEye(Move.getMoveInstance(MoveType.PASS, 0, 4), white.getColor()));
		assertFalse(b.isEye(Move.getMoveInstance(MoveType.PASS, 2, 2), white.getColor()));
	}

}

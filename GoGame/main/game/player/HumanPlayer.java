package game.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import enums.MoveType;
import enums.PlayerColor;
import game.model.Board;
import game.model.Move;

public class HumanPlayer extends Player{
	
	public HumanPlayer(PlayerColor c){
		super(c);
	}

	@Override
	public Move getMove(Board board)  {
		System.out.println(board.toString());
		System.out.println("Enter your move (x,y): ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String[] inputs = in.readLine().split(",");
			return new Move(MoveType.NORMAL, new Integer(inputs[0]), new Integer(inputs[1]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return new Move(MoveType.PASS, null, null);
		}
	}
}

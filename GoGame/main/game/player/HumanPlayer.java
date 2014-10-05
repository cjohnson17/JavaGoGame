package game.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import app.GameState;
import enums.MoveType;
import enums.PlayerColor;
import game.model.Board;
import game.model.Move;

public class HumanPlayer extends Player{
	
	public HumanPlayer(PlayerColor c){
		super(c);
	}

	@Override
	public Move getMove(GameState state)  {
		System.out.println(state.toString());
		System.out.println("Enter your move (x,y): ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String[] inputs = in.readLine().split(",");
			if (inputs[0].equals("pass")){
				return Move.getMoveInstance(MoveType.PASS, 0, 0);
			}
			return Move.getMoveInstance(MoveType.NORMAL, new Integer(inputs[0]), new Integer(inputs[1]));
		} catch (IOException e) {
			return null;
		}
	}
}

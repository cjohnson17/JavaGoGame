package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.model.Board;
import game.model.Move;
import game.player.HumanPlayer;
import game.player.MCTSComputerPlayer;
import game.player.Player;
import game.player.SimpleComputerPlayer;

//proof of concept Go Game Runner
public class AppRunner {
	public static void main(String[] args) throws NumberFormatException, MoveException, IOException{
		Queue<Player> players = createPlayers();
		GameState game = new GameState(new ArrayList<>(players));
		int passes = 0;
		do {
			Player currentPlayer = game.getNextToMove();
			Move move = currentPlayer.getMove(game);
			if (move.getType().equals(MoveType.PASS)){
				passes++;
			} else {
				passes = 0;
			}
			game.addMove(move);
		} while (passes < 2);
		
		System.out.println("Game Over: " + "\n" + game.toString());
	}

	//Great place for Spring here
	private static Queue<Player> createPlayers() {
		Queue<Player> players = new ArrayBlockingQueue<>(2);
		players.add(new MCTSComputerPlayer(PlayerColor.BLACK));
		players.add(new HumanPlayer(PlayerColor.WHITE));
		
		return players;
	}
}

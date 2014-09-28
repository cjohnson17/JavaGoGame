package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.model.Board;
import game.model.Move;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.SimpleComputerPlayer;

//proof of concept Go Game Runner
public class AppRunner {
	public static void main(String[] args) throws NumberFormatException, MoveException, IOException{
		Board board = new Board(4);
		Queue<Player> players = getPlayers();
		int passes = 0;
		do {
			Player currentPlayer = players.poll();
			Move move = currentPlayer.getMove(board);
			if (move.getType().equals(MoveType.PASS)){
				passes++;
			} else {
				passes = 0;
				board.makeMove(move, currentPlayer.getColor());
			}
			players.add(currentPlayer);
		} while (passes < 2);
		
		System.out.println("Game Over, final score: " + board.getScore());
	}

	//Great place for Spring here
	private static Queue<Player> getPlayers() {
		Queue<Player> players = new ArrayBlockingQueue<>(2);
		players.add(new HumanPlayer(PlayerColor.BLACK));
		players.add(new SimpleComputerPlayer(PlayerColor.WHITE));
		
		return players;
	}

	private static String getInput(PlayerColor currentPlayer) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		return in.readLine();
	}
}

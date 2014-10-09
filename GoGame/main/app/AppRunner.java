package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import enums.MoveType;
import enums.PlayerColor;
import exception.MoveException;
import game.model.Move;
import game.player.HumanPlayer;
import game.player.MCTSComputerPlayer;
import game.player.Player;
import game.player.SimpleComputerPlayer;

//proof of concept Go Game Runner
public class AppRunner {
	public static final int BOARD_SIZE = 9;
	
	public static void main(String[] args) throws NumberFormatException, MoveException, IOException{
		Queue<Player> players = createPlayers();
		GameState game = new GameState(BOARD_SIZE, new ArrayList<>(players));
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
			System.out.println(game.toString());
		} while (passes < 2);
		game.captureDeadGroups();
		Map<PlayerColor, Integer> finalScore = game.getScore();
		System.out.println("Final Score: " + finalScore);
	}

	//Great place for Spring here
	private static Queue<Player> createPlayers() {
		Queue<Player> players = new ArrayBlockingQueue<>(2);
		players.add(new MCTSComputerPlayer(PlayerColor.BLACK,10000));
		players.add(new MCTSComputerPlayer(PlayerColor.WHITE, 100));
		
		return players;
	}
}

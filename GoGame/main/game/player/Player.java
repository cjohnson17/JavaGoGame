package game.player;

import enums.PlayerColor;
import game.model.Board;
import game.model.Move;

public abstract class Player {

	PlayerColor color;
	
	public Player(PlayerColor color) {
		this.color = color;
	}
	
	public abstract Move getMove(Board board);

	public PlayerColor getColor() {
		return color;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}
	
}

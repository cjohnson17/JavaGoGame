package game.model;

import enums.MoveType;

public class Move {
	private MoveType type;
	private Integer x;
	private Integer y;

	public Move(MoveType type, Integer x, Integer y){
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public MoveType getType() {
		return type;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}
}

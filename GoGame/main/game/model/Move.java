package game.model;

import java.util.HashMap;
import java.util.Map;

import enums.MoveType;

public class Move {
	
	private MoveType type;
	private Integer x;
	private Integer y;
	
	public static final int MAX_BOARD_SIZE = 19;
	
	private static Map<MoveType, Move[][]> moves;
	
	public static Move getMoveInstance(MoveType type, int x, int y){
		if (moves == null){
			moves = new HashMap<>();
			for (MoveType t : MoveType.values()){
				Move[][] moveArray = new Move[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
				for (int i = 0; i<MAX_BOARD_SIZE; i++){
					for (int j = 0; j<MAX_BOARD_SIZE; j++){
						moveArray[i][j] = new Move(t, i, j);
					}
				}
				moves.put(t, moveArray);
			}
		}
		return moves.get(type)[x][y];
	}
	
	private Move(MoveType type, Integer x, Integer y){
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

	@Override
	public int hashCode() {
		return x*MAX_BOARD_SIZE+y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (type != other.type)
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("(" + x + "," + y + ")");
	}
}

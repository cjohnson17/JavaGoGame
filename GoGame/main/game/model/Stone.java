package game.model;

import enums.PlayerColor;

public class Stone {

	private PlayerColor owner;
	private StoneGroup group;
	public int x_location;
	public int y_location;


	public Stone(int x, int y, PlayerColor player) {
		setOwner(player);
		x_location = x;
		y_location = y;
		group = new StoneGroup(this);
	}
	
	public boolean isConnected(Stone stone) {
		return group.contains(stone);
	}

	public void connect(Stone newStone) {
		if (owner.equals(newStone.getOwner()) && !group.includes(newStone)){
			group.merge(newStone.getGroup());
		}
	}

	public PlayerColor getOwner() {
		return owner;
	}

	public void setOwner(PlayerColor owner) {
		this.owner = owner;
	}

	public StoneGroup getGroup() {
		return group;
	}

	public void setGroup(StoneGroup group) {
		this.group = group;
	}
	
	
	@Override
	public String toString() {
		return "Stone [owner=" + owner + ", x_location="
				+ x_location + ", y_location=" + y_location + "]";
	}

}

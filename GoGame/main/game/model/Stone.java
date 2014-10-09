package game.model;

import enums.PlayerColor;

public class Stone {

	//the color of this stone
	private PlayerColor owner;
	
	//The group of stones this stone is connected to. A single stone is in a group that only contains 
	//itself. Note this relationship is bi-directional, so make sure to correctly update references
	//when adding or deleting stones to a group
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

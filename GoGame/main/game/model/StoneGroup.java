package game.model;

import java.util.HashSet;
import java.util.Set;

import enums.PlayerColor;

public class StoneGroup {

	private Set<Stone> stones;
	private int remainingLiberties;
	private PlayerColor owner;
	
	public StoneGroup(Stone stone) {
		stones = new HashSet<Stone>();
		stones.add(stone);
		owner = stone.getOwner();
	}

	public boolean contains(Stone stone) {
		return stones.contains(stone);
	}

	public void add(Stone newStone) {
		stones.add(newStone);
	}
	
	public void addAll(Set<Stone> stones) {
		for (Stone stone : stones){
			stone.setGroup(this);
		}
		stones.addAll(stones);
	}

	public boolean includes(Stone newStone) {
		return stones.contains(newStone);
	}

	public int getRemainingLiberties() {
		return remainingLiberties;
	}

	public void setRemainingLiberties(int remainingLiberties) {
		this.remainingLiberties = remainingLiberties;
	}

	public Set<Stone> getStones() {
		return stones;
	}
	
	public int size() {
		return stones.size();
	}
	
	public StoneGroup merge(StoneGroup otherGroup) {
		StoneGroup largerGroup = this.size() > otherGroup.size() ? this : otherGroup;
		StoneGroup smallerGroup = largerGroup == this ? otherGroup : this;
		for (Stone stone : smallerGroup.getStones()){
			stone.setGroup(largerGroup);
			largerGroup.add(stone);
		}
		smallerGroup.stones.clear();
		return largerGroup;
	}

	public PlayerColor getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		return "StoneGroup [stones=" + stones + ", remainingLiberties="
				+ remainingLiberties + "]";
	}
	
	

}

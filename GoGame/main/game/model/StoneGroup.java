package game.model;

import java.util.HashSet;
import java.util.Set;

import enums.PlayerColor;

public class StoneGroup {

	private Set<Stone> stones;
	private Set<Intersection> liberties;
	private Set<StoneGroup> surroundingStoneGroups;
	private PlayerColor owner;
	
	public StoneGroup(Stone stone) {
		stones = new HashSet<Stone>();
		stones.add(stone);
		owner = stone.getOwner();
		liberties = new HashSet<Intersection>();
		surroundingStoneGroups = new HashSet<>();
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

	public Integer getRemainingLiberties() {
		return liberties.size();
	}

	public Set<Intersection> getLiberties() {
		return liberties;
	}

	public void setLiberties(Set<Intersection> liberties) {
		this.liberties = liberties;
	}

	public void removeSurroundingStoneGroups(StoneGroup surroundingStoneGroup) {
		surroundingStoneGroups.remove(surroundingStoneGroup);
	}
	
	public Set<StoneGroup> getSurroundingStoneGroups() {
		return surroundingStoneGroups;
	}

	public void addSurroundingStoneGroups(StoneGroup surroundingStoneGroup) {
		surroundingStoneGroups.add(surroundingStoneGroup);
	}

	public Set<Stone> getStones() {
		return stones;
	}
	
	public int size() {
		return stones.size();
	}
	
	/**
	 * Combines two StoneGroups together. The smaller group (fewer stones) is "destroyed",
	 * and all the stones that were in the smaller group are put into the larger group
	 * @param otherGroup	the group to be merged
	 * @return				the group that now holds all the stones
	 */
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
				+ getRemainingLiberties() + "]";
	}
	
	

}

package game.model;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
	//null if the intersection is empty
	private Stone occupant;

	private List<Intersection> neighbors;

	public int x_location;
	public int y_location;

	public Intersection (int x, int y){
		x_location = x;
		y_location = y;
	}

	public Stone getOccupant() {
		return occupant;
	}

	public void setOccupant(Stone occupant) {
		this.occupant = occupant;
	}

	public boolean isOccupied() {
		return this.occupant != null;
	}

	public List<Intersection> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(List<Intersection> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(Intersection neighbor){
		if (neighbors == null){
			neighbors = new ArrayList<>();
		}
		neighbors.add(neighbor);
	}
	
	@Override
	public String toString() {
		String occupantString = occupant == null ? "empty" : occupant.getOwner().toString();
	
		return "Intersection [occupant=" + occupantString + ", x_location=" + x_location + ", y_location=" + y_location + "]";
	}


}

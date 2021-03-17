package Mine.Sweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.morphia.Datastore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
public class Cell {
	@Transient
	private Grid parent;
	@Getter @Id private Point position;
	@Getter @Setter
	private boolean revealed = false, bomb = false;
	
	private Cell(){}
	
	@Accessors(chain = true)
	public static class Builder{
		Builder(){}
		Builder(Cell defaults){
			parent = defaults.parent;
			position = defaults.position;
			revealed = defaults.revealed;
			bomb = defaults.bomb;
		}
		@Setter private Grid parent;
		@Setter private Point position;
		private boolean revealed = false, bomb = false;
		public Cell build() {
			Cell cell = new Cell();
			cell.parent = parent;
			cell.position = position;
			cell.revealed = revealed;
			cell.bomb = bomb;
			return cell;
		}
	}
	
	private Cell getNorth() {return parent.map.get(new Point().set(position.x(), position.y()-1));}
	private Cell getSouth() {return parent.map.get(new Point().set(position.x(), position.y()+1));}
	private Cell getEast() {return parent.map.get(new Point().set(position.x()+1, position.y()));}
	private Cell getWest() {return parent.map.get(new Point().set(position.x()-1, position.y()));}
	
	public String toString() {
		return revealed? bomb? "X" : String.valueOf(getNumberOfBombsSurroundingCell()) : "?";
	}

	protected List<Cell> getSurroundingCells(){
		List<Cell> surroundingCells = new ArrayList<Cell>();
		
		if(getNorth() != null) {
			surroundingCells.add(getNorth().getWest());
			surroundingCells.add(getNorth());
			surroundingCells.add(getNorth().getEast());
		}
		
			surroundingCells.add(getWest());
			surroundingCells.add(getEast());
		
		if(getSouth() != null) {
			surroundingCells.add(getSouth().getWest());
			surroundingCells.add(getSouth());
			surroundingCells.add(getSouth().getEast());
		}
		
		while (surroundingCells.remove(null));
		return surroundingCells;
	}
	protected int getNumberOfBombsSurroundingCell() {
		return (int) getSurroundingCells().stream().filter(Objects::nonNull).filter(Cell::isBomb).count();
	}
}

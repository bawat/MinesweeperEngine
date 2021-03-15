package Mine.Sweeper;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.morphia.annotations.Entity;
import lombok.Getter;
import lombok.Setter;

public class Cell {
	private Grid parent;
	@Getter private Point2D position;
	@Getter @Setter
	private boolean revealed = false, bomb = false;
	
	public Cell(Point2D position, Grid parent) {
		this.position = position;
		this.parent = parent;
	}
	
	private Cell getNorth() {return parent.map.get(new Point2D.Double(position.getX(), position.getY()-1));}
	private Cell getSouth() {return parent.map.get(new Point2D.Double(position.getX(), position.getY()+1));}
	private Cell getEast() {return parent.map.get(new Point2D.Double(position.getX()+1, position.getY()));}
	private Cell getWest() {return parent.map.get(new Point2D.Double(position.getX()-1, position.getY()));}
	
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

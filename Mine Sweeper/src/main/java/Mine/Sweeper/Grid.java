package Mine.Sweeper;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import lombok.Getter;

@Entity
public class Grid {
	@Reference
	protected HashMap<Point2D, Cell> map = new HashMap<Point2D, Cell>();
	@Getter
	private int width, height;
	
	public Grid(int width, int height, int bombs){
		this.width = width;
		this.height = height;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Point2D point = new Point2D.Double(x,  y); 
				map.put(point, new Cell(point, this));
			}
		}
		addBombs(bombs);
	}
	
	private void addBombs(int bombs) {
		List<Cell> values = new ArrayList<>(map.values());
		Collections.shuffle(values);
		for(Cell value : values) {
			if(bombs-- <= 0) return;
			value.setBomb(true);
		}
	}
	
	public void revealCell(Point2D point) {
		if(!map.containsKey(point)) return;
		map.get(point).setRevealed(true);
		
		revealingSurroundingCells(point);
	}
	
	private void revealingSurroundingCells(Point2D point){
		Cell cell = map.get(point);
		cell.getSurroundingCells().stream()
			.filter(e -> !e.isRevealed())
			.forEach(e -> {
				if(e.isBomb()) return;
				e.setRevealed(true);
				if(e.getNumberOfBombsSurroundingCell() == 0) revealingSurroundingCells(e.getPosition());
			});
	}
	
	public void drawToConsole() {
		String b = ""
		+ "#".repeat(getWidth()) + System.lineSeparator()
		+ "Here is your Minesweeper:" + System.lineSeparator()
		+ "#".repeat(getWidth()) + System.lineSeparator();

		for(int y = 0; y < getHeight(); y++) {
			for(int x = 0; x < getWidth(); x++) {
				b += map.get(new Point2D.Double(x, y));
			}
			b += System.lineSeparator();
		}
		
		b += "#".repeat(getWidth()) + System.lineSeparator();
		
		System.out.println(b);
	}

	public boolean gameIsntOver() {
		return !gameLost() && !gameWon();
	}
	private boolean gameLost() {
		return map.values().stream().filter(Cell::isBomb).filter(Cell::isRevealed).findAny().isPresent();
	}
	public boolean gameWon() {
		return map.values().stream().allMatch(e -> e.isBomb() ^ e.isRevealed());
	}
}

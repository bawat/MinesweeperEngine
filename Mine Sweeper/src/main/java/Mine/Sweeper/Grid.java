package Mine.Sweeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dev.morphia.Datastore;
import lombok.Getter;
import lombok.SneakyThrows;

public class Grid {
	public HashMap<Point, Cell> map = new HashMap<Point, Cell>();
	@Getter
	private int width, height;
	
	public Grid(int width, int height, int bombs){
		this.width = width;
		this.height = height;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Point point = new Point().set(x,  y); 
				map.put(point, new Cell.Builder()
									.setPosition(point)
									.setParent(this)
									.build()
						);
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
	public int countBombs() {
		return (int) map.values().stream().filter(Cell::isBomb).count();
	}
	
	public void revealCell(Point point) {
		if(!map.containsKey(point)) return;
		map.get(point).setRevealed(true);
		
		revealingSurroundingCells(point);
	}
	
	private void revealingSurroundingCells(Point point){
		Cell cell = map.get(point);
		cell.getSurroundingCells().stream()
			.filter(e -> !e.isRevealed())
			.forEach(e -> {
				if(e.isBomb()) return;
				e.setRevealed(true);
				if(e.getNumberOfBombsSurroundingCell() == 0) revealingSurroundingCells(e.getPosition());
			});
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

	@SneakyThrows
	public ThreadPoolExecutor uploadAsync(Datastore datastore) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		for(Cell cell : map.values()) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					datastore.save(cell);
				}
			});
		}
		executor.shutdown();
		return executor;
	}
	@SneakyThrows
	public void uploadSync(Datastore datastore) {
		uploadAsync(datastore).awaitTermination(30, TimeUnit.SECONDS);
	}
	public void download(Datastore datastore) {
		map.clear();
		for(Cell cell : datastore.createQuery(Cell.class).find().toList()) {
			Cell newCell = new Cell.Builder(cell).setParent(this).build();
			map.put(newCell.getPosition(), newCell);
		}
	}
}

package Mine.Sweeper.Drawing;

import java.util.Scanner;

import Mine.Sweeper.Grid;
import Mine.Sweeper.Point;

public class ConsoleUI implements UI{

	@Override
	public void update(Grid engine) {
		String b = ""
		+ "#".repeat(engine.getWidth()) + System.lineSeparator()
		+ "Number of bombs: " + engine.countBombs() + System.lineSeparator()
		+ "#".repeat(engine.getWidth()) + System.lineSeparator();

		for(int y = 0; y < engine.getHeight(); y++) {
			for(int x = 0; x < engine.getWidth(); x++) {
				b += engine.map.get(new Point().set(x,y));
			}
			b += System.lineSeparator();
		}
		
		b += "#".repeat(engine.getWidth()) + System.lineSeparator();
		
		System.out.println(b);
	}

	private static Scanner scanner = new Scanner(System.in);
	@Override
	public Point blockForInput() {
		System.out.println("Type a coordinate to check...  e.g. x,y");
		String[] coordinate = scanner.next().split(",");
		return new Point().set(Integer.valueOf(coordinate[0]),Integer.valueOf(coordinate[1]));
	}
	@Override
	public void gameComplete(boolean win) {
		System.out.println(win? "Congrats! You won!" : "Better luck next time...");
		scanner.close();
	}

}

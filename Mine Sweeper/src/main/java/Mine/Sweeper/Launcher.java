package Mine.Sweeper;

import java.awt.geom.Point2D;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class Launcher {
	public static void main(String[] args) {
		Grid engine = new Grid(20, 10, 4);
		engine.drawToConsole();
		
		Scanner scanner = new Scanner(System.in);
		while(engine.gameIsntOver()) {
			System.out.println("Type a coordinate to check...  e.g. x,y");
			String[] coordinate = scanner.next().split(",");
			Point2D point = new Point2D.Double(
					Integer.valueOf(coordinate[0]),
					Integer.valueOf(coordinate[1])
				);
			
			engine.revealCell(point);
			engine.drawToConsole();
		}
		scanner.close();
		
		System.out.println(engine.gameWon()? "Congrats! You won!" : "Better luck next time...");
	}
}

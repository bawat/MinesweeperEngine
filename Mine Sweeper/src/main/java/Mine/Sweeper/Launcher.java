package Mine.Sweeper;

import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.SneakyThrows;

public class Launcher {
	@SneakyThrows
	public static void main(String[] args) {
		MongoClientURI uri = new MongoClientURI(System.getenv("MINESWEEPER_MONGODB_CONNECTION"));
		MongoClient mongoClient = new MongoClient(uri);
		
		Morphia morphia = new Morphia();
		morphia.mapPackage("Mine.Sweeper");
		
		Datastore datastore = morphia.createDatastore(mongoClient, "minesweeperStore");
		datastore.ensureIndexes();
		
		Grid engine = new Grid(20, 10, 10);
		if(datastore.find(Cell.class).count() > 0) {
			System.out.println("Loading from database... ");
			engine.download(datastore);
		}
		
		engine.drawToConsole();
		
		Scanner scanner = new Scanner(System.in);
		ThreadPoolExecutor uploadTask = null;
		while(engine.gameIsntOver()) {
			System.out.println("Type a coordinate to check...  e.g. x,y");
			if(uploadTask != null) uploadTask.awaitTermination(30, TimeUnit.SECONDS);
			String[] coordinate = scanner.next().split(",");
			Point point = new Point().set(Integer.valueOf(coordinate[0]),Integer.valueOf(coordinate[1]));
			
			engine.revealCell(point);
			
			uploadTask = engine.uploadAsync(datastore);
			engine.drawToConsole();
		}
		scanner.close();
		
		System.out.println(engine.gameWon()? "Congrats! You won!" : "Better luck next time...");
		
		engine = new Grid(20, 10, 10);
		engine.uploadSync(datastore);
		System.out.println("Goodbye.");
	}
}

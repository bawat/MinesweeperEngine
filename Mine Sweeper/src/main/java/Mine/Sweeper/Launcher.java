package Mine.Sweeper;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClientURI;

import Mine.Sweeper.Drawing.*;

import com.mongodb.MongoClient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.SneakyThrows;

public class Launcher{
	private static Datastore datastore;
	static {
		MongoClientURI uri = new MongoClientURI(System.getenv("MINESWEEPER_MONGODB_CONNECTION"));
		MongoClient mongoClient = new MongoClient(uri);
		Morphia morphia = new Morphia();
		morphia.mapPackage("Mine.Sweeper");
		
		datastore = morphia.createDatastore(mongoClient, "minesweeperStore");
		datastore.ensureIndexes();
	}
	
	
	public static void main(String[] args) {
		try {
			play(new Grid(20, 10, 10), new /*ConsoleUI*/GUI());
		} catch(com.mongodb.MongoConfigurationException e) {
			System.err.println("Please port forward the port 27017");
		}
	}
	@SneakyThrows
	private static void play(Grid engine, UI ui) {
		if(datastore.find(Cell.class).count() > 0) {
			System.out.println("Loading from database... ");
			engine.download(datastore);
		}
		
		ui.update(engine);
		
		ThreadPoolExecutor uploadTask = null;
		while(engine.gameIsntOver()) {
			if(uploadTask != null) uploadTask.awaitTermination(30, TimeUnit.SECONDS);
			
			
			engine.revealCell(ui.blockForInput());
			
			uploadTask = engine.uploadAsync(datastore);
			ui.update(engine);
		}
		
		ui.gameComplete(engine.gameWon());
		
		engine = new Grid(20, 10, 10);
		engine.uploadSync(datastore);
		System.out.println("Goodbye.");
	}
}
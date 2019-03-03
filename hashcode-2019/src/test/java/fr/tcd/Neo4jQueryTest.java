package fr.tcd;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.core.NodeProxy;

public class Neo4jQueryTest {

	@Test
	public void queryTest() {
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(new File("/home/gafou/projets/perso/data/a_exemple"));
		registerShutdownHook(graphDb);
		String query = DoTheThingService.selectPhotosHorizWithMostTags;
		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(query)) {
			String rows = "Start result\n";
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				for (Entry<String, Object> column : row.entrySet()) {
					if ("photos".equals(column.getKey())) {
						ArrayList<NodeProxy> photos = (ArrayList) column.getValue();
						for (NodeProxy photo : photos) {
							rows += "photoId: " + photo.getProperty("photoId") + "; ";
							rows += "photoOrientation: " + photo.getProperty("photoOrientation") + "; ";
						}
					} else {
						rows += column.getKey() + ": " + column.getValue() + "; ";
					}
				}
				rows += "\n";
			}
			System.out.println(rows);
			tx.success();
		}
		graphDb.shutdown();
	}

	@Test
	public void queryTest2() {
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(new File("/home/gafou/projets/perso/data/a_exemple"));
		registerShutdownHook(graphDb);

		String firstPhoto = null;
		String secondPhoto = null;
		String tagname = null;
		boolean valid = false;

		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(Main.selectTagWithMostPhotos)) {
			while (result.hasNext() && !valid) {
				Map<String, Object> row = result.next();
				tagname = (String) row.get("tagname");
				ArrayList<NodeProxy> photos = (ArrayList) row.get("photos");
				for (NodeProxy photo : photos) {
					if (firstPhoto == null) {
						firstPhoto = (String) photo.getProperty("photoId");
						if ("V".equals(photo.getProperty("photoOrientation"))) {
							valid = true;
						}
					} else if (secondPhoto == null && !valid) {
						if ("H".equals(photo.getProperty("photoOrientation"))) {
							secondPhoto = (String) photo.getProperty("photoId");
							valid = true;
						}
					}
				}
				if(!valid) {
					firstPhoto = null;
					secondPhoto = null;
					tagname = null;
				}
			}
			tx.success();
		}
		graphDb.shutdown();
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
}

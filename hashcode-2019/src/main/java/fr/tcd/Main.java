package fr.tcd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.kernel.impl.core.NodeProxy;

public class Main {

	public static List<String> nbResults = new ArrayList<String>();
	public static List<String> results = new ArrayList<String>();

	public static String selectTagWithMostPhotos = "MATCH (t:Tag)-[:HAS_PHOTO]->(p:Photo{used:false}) \n"
			+ "WITH t,count(p) as rels, collect(p) as photos\n" + "WHERE rels >= 1 \n"
			+ "RETURN t.tagname as tagname , photos, rels\n" + "ORDER BY rels DESC LIMIT 1";

	public static String selectOtherPhoto = "MATCH (p1:Photo{used:false}) \n"
			+ "WHERE p1.photoId <> MY_ID AND p1.photoOrientation = 'V' \n" + "RETURN p1.photoId as photoId";

	public static String updateToInitial = "MATCH (p:Photo{used:true})\n" + "WITH p\n" + "SET p.used = false \n"
			+ "RETURN 1";

	public static String tags = "MATCH (t:Tag) \n" + "RETURN t.tagname";

	public static String label = "MATCH (n) \n" + "RETURN distinct labels(n)";

	public static void main(String[] args) throws IOException {

		System.out.println("do the thing ... maybe");

		System.out.println("start neo4j database ...  why not");
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(new File("/home/gafou/projets/perso/data/" + args[0]));
		registerShutdownHook(graphDb);

		initDatabase(graphDb);

		System.out.println("load ans inject data in neo4j databas ...  why not");
		try {
			DataService.initData(args[1], graphDb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// select one slideshow
		/*try {
			while (true) {
				doTheNewThing(graphDb);
			}
		} catch (EndedException e) {
			nbResults.add("" + results.size());
			nbResults.addAll(results);
			FileUtils.writeLines(new File(args[0] + ".out"), nbResults);
		}*/
		
		DoTheThingService doTheThingService = new DoTheThingService();
		results.addAll(doTheThingService.doTheThing(graphDb));
		nbResults.add("" + results.size());
		nbResults.addAll(results);
		FileUtils.writeLines(new File(args[0] + ".out"), nbResults);

		System.out.println("shut neo4j down ...  yeahhh we should");
		graphDb.shutdown();

	}

	private static void initDatabase(GraphDatabaseService graphDb) {
		IndexDefinition indexDefinition;
		try (Transaction tx = graphDb.beginTx()) {
			Schema schema = graphDb.schema();
			try {
				schema.getIndexByName("TagIndex");
			} catch (java.lang.IllegalArgumentException e) {
				indexDefinition = schema.indexFor(Label.label("Tag")).on("tagname").withName("TagIndex").create();
			}

			tx.success();
		}
		try (Transaction tx = graphDb.beginTx()) {
			Schema schema = graphDb.schema();
			try {
				schema.getIndexByName("PhotoIndex");
			} catch (java.lang.IllegalArgumentException e) {
				indexDefinition = schema.indexFor(Label.label("Photo")).on("photoId").withName("PhotoIndex").create();
			}
			tx.success();
		}

	}

	private static void doTheNewThing(GraphDatabaseService graphDb) throws EndedException {
		System.err.println("do the new thing");
		// find photos
		Integer firstPhotoId = null;
		String firstPhotoOrientation = null;
		Integer adjacentFirstPhotoId = null;
		Integer secondPhotoId = null;
		String secondPhotoOrientation = null;
		Integer adjacentSecondPhotoId = null;
		String tagname = null;
		boolean valid = false;

		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(Main.selectTagWithMostPhotos)) {
			if (!result.hasNext()) {
				throw new EndedException();
			}
			while (result.hasNext() && !valid) {
				Map<String, Object> row = result.next();
				tagname = (String) row.get("tagname");
				ArrayList<NodeProxy> photos = (ArrayList) row.get("photos");
				for (NodeProxy photo : photos) {
					if (firstPhotoId == null) {
						// System.out.println(photo.hasProperty("photoId"));
						firstPhotoId = (Integer) photo.getProperty("photoId");
						firstPhotoOrientation = (String) photo.getProperty("photoOrientation");
					} else if (secondPhotoId == null) {
						secondPhotoId = (Integer) photo.getProperty("photoId");
						secondPhotoOrientation = (String) photo.getProperty("photoOrientation");
						valid = true;
					}
				}
				if (!valid) {
					firstPhotoId = null;
					secondPhotoId = null;
					tagname = null;
				}
			}
			tx.success();
		}
		if (!valid) {
			throw new EndedException();
		}

		if ("V".equals(firstPhotoOrientation)) {
			// find new photo
			String query = Main.selectOtherPhoto.replaceFirst("MY_ID", "" + firstPhotoId);
			try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(query)) {
				if (result.hasNext()) {
					Map<String, Object> row = result.next();
					adjacentFirstPhotoId = (Integer) row.get("photoId");
				}
			}
		}

		// System.out.println(firstPhotoId+" "+adjacentFirstPhotoId);

		// tag photos as used
		try (Transaction tx = graphDb.beginTx()) {
			Label photoLabel = Label.label("Photo");
			Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", firstPhotoId);
			firstPhotoNode.setProperty("used", true);
			if (adjacentFirstPhotoId != null) {
				Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", adjacentFirstPhotoId);
				secondPhotoNode.setProperty("used", true);
			}
			tx.success();
		}

		if ("V".equals(secondPhotoOrientation)) {
			// find new photo
			String query = Main.selectOtherPhoto.replaceFirst("MY_ID", "" + secondPhotoId);
			try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(query)) {
				if (result.hasNext()) {
					Map<String, Object> row = result.next();
					adjacentSecondPhotoId = (Integer) row.get("photoId");
				}
			}
		}

		// System.out.println(secondPhotoId+" "+adjacentSecondPhotoId);

		// tag photos as used
		try (Transaction tx = graphDb.beginTx()) {
			Label photoLabel = Label.label("Photo");
			Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", secondPhotoId);
			firstPhotoNode.setProperty("used", true);
			if (adjacentSecondPhotoId != null) {
				Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", adjacentSecondPhotoId);
				secondPhotoNode.setProperty("used", true);
			}
			tx.success();
		}

		// System.out.println(tagname + " " + firstPhotoId + " " +
		// adjacentFirstPhotoId);
		if (tagname != null && firstPhotoId != null) {
			String result = "" + firstPhotoId;
			if (adjacentFirstPhotoId != null) {
				result += " " + adjacentFirstPhotoId;
			}
			if("H".equals(firstPhotoOrientation) || adjacentFirstPhotoId != null) {
				results.add(result);
			}
		}
		if (tagname != null && secondPhotoId != null && !secondPhotoId.equals(adjacentFirstPhotoId)
				&& !firstPhotoId.equals(adjacentSecondPhotoId)) {
			String result = "" + secondPhotoId;
			if (adjacentSecondPhotoId != null) {
				result += " " + adjacentSecondPhotoId;
			}
			if("H".equals(secondPhotoOrientation) || adjacentSecondPhotoId != null) {
				results.add(result);
			}
		}
	}

	private static void doTheThing(GraphDatabaseService graphDb) throws EndedException {

		// find photos
		Integer firstPhoto = null;
		Integer secondPhoto = null;
		String tagname = null;
		boolean valid = false;

		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(Main.selectTagWithMostPhotos)) {
			if (!result.hasNext()) {
				throw new EndedException();
			}
			while (result.hasNext() && !valid) {
				Map<String, Object> row = result.next();
				tagname = (String) row.get("tagname");
				ArrayList<NodeProxy> photos = (ArrayList) row.get("photos");
				for (NodeProxy photo : photos) {
					if (firstPhoto == null) {
						System.out.println(photo.hasProperty("photoId"));
						firstPhoto = (Integer) photo.getProperty("photoId");
						if ("H".equals(photo.getProperty("photoOrientation"))) {
							valid = true;
						}
					} else if (secondPhoto == null && !valid) {
						if ("V".equals(photo.getProperty("photoOrientation"))) {
							secondPhoto = (Integer) photo.getProperty("photoId");
							valid = true;
						}
					}
				}
				if (!valid) {
					firstPhoto = null;
					secondPhoto = null;
					tagname = null;
				}
			}
			tx.success();
		}

		// tag photos as used
		try (Transaction tx = graphDb.beginTx()) {
			Label photoLabel = Label.label("Photo");
			Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", firstPhoto);
			firstPhotoNode.setProperty("used", true);
			if (secondPhoto != null) {
				Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", secondPhoto);
				secondPhotoNode.setProperty("used", true);
			}
			tx.success();
		}

		// add photos to result
		System.out.println(tagname + " " + firstPhoto + " " + secondPhoto);
		if (tagname != null && firstPhoto != null) {
			String result = "" + firstPhoto;
			if (secondPhoto != null) {
				result += " " + secondPhoto;
			}
			results.add(result);
		}
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

	private static void logDebug(String message, Object... params) {
		// System.out.printf(message, params);
	}

}
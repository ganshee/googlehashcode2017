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
			DataInitService.initData(args[1], graphDb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
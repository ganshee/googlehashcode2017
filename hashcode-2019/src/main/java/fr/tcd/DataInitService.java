package fr.tcd;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class DataInitService {
	
	public static String updateToInitial = "MATCH (p:Photo{used:true})\n" + "WITH p\n" + "SET p.used = false \n"
			+ "RETURN 1";

	public static void initData(final String filename, GraphDatabaseService graphDb) throws FileNotFoundException {
		System.err.println("start initData at " + Instant.now().toString());

		final Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(filename));

		final int nbPhotos = in.nextInt();
		System.out.printf("nbPhotos : %d \n", nbPhotos);
		in.nextLine();
		List<Photo> photos = new ArrayList<Photo>();


		if (alreadyExistsDatabase(graphDb)) {
			// update database
			try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(Main.updateToInitial)) {
				tx.success();
			}
			return;
		}
		
		for (int numPhoto = 0; numPhoto < nbPhotos; numPhoto++) {
			String photoLine = in.nextLine();
			String[] datas = photoLine.split(" ");

			Photo photo = new Photo();
			photo.photoId = numPhoto;
			photo.photoOrientation = datas[0];
			photos.add(photo);

			Integer photoNumTags = Integer.valueOf(datas[1]);
			for (int numTag = 0; numTag < photoNumTags; numTag++) {
				photo.tags.add(datas[numTag + 2]);
			}

			if (photos.size() == 50) {
				System.out.println("insert 50");
				insertPhotosAndTags(photos, graphDb);
				photos.clear();
			}
		}
		if (photos.size() > 0) {
			System.out.println("insert last");
			insertPhotosAndTags(photos, graphDb);
			photos.clear();
		}
		System.err.println("end initData at " + Instant.now().toString());
	}

	private static void insertPhotosAndTags(List<Photo> photos, GraphDatabaseService graphDb) {


		try (Transaction tx = graphDb.beginTx()) {
			RelationshipType hasTag = RelationshipType.withName("HAS_TAG");
			//RelationshipType hasPhoto = RelationshipType.withName("HAS_PHOTO");
			Label photoLabel = Label.label("Photo");
			Label tagLabel = Label.label("Tag");

			Map<String, Node> tagNodes = new HashMap<String, Node>();
			for (Photo photo : photos) {
				Node photoNode = graphDb.findNode(photoLabel, "photoId", photo.photoId);
				if (photoNode == null) {
					logDebug("create photo : %d %s \n", photo.photoId, photo.photoOrientation);
					photoNode = graphDb.createNode(photoLabel);
					photoNode.setProperty("photoId", photo.photoId);
					photoNode.setProperty("photoOrientation", photo.photoOrientation);
					photoNode.setProperty("used", false);
				}
				for (String tagname : photo.tags) {
					Node tagNode = tagNodes.get(tagname);
					if (tagNode == null) {
						tagNode = graphDb.findNode(tagLabel, "tagname", tagname);
						if (tagNode == null) {
							logDebug("create tag : %s \n", tagname);
							tagNode = graphDb.createNode(tagLabel);
							tagNode.setProperty("tagname", tagname);
						}
						tagNodes.put(tagname, tagNode);
					}
					logDebug("link tag to photo: %s %d \n", tagname, photo.photoId);
					photoNode.createRelationshipTo(tagNode, hasTag);
					//tagNode.createRelationshipTo(photoNode, hasPhoto);

				}
			}

			tx.success();
		}

	}

	private static boolean alreadyExistsDatabase(GraphDatabaseService graphDb) {
		long count = 0;
		try (Transaction tx = graphDb.beginTx()) {
			Result result = graphDb.execute("MATCH (n:Photo) RETURN count(n.photoId) AS nbPhotos");
			if (result.hasNext()) {
				Map<String, Object> countResult = result.next();
				count = (long) countResult.get("nbPhotos");
			}
			tx.success();
		}
		return count > 0;
	}

	private static void logDebug(String message, Object... params) {
		//System.out.printf(message, params);
	}
}

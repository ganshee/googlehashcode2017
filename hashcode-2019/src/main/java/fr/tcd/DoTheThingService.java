package fr.tcd;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class DoTheThingService {

	public static String updatePhotoAsUsed = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = true \n"
			+ "RETURN p.photoId as pId";

	public static String updatePhotoAsUnused = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = false \n"
			+ "RETURN p.photoId as pId";

	@Deprecated
	public static String selectPhotosHorizWithMostTags = "MATCH (p1:Photo{used:false,photoOrientation:'H'})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH p1.photoId as p1Id, p2.photoId as p2Id, count(distinct t) as numTags\n"
			+ "RETURN p1Id, p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public static String selectPhotosVertiWithMostTagsFromID = "MATCH (p1:Photo{used:true,photoOrientation:'V',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p2.photoId as p2Id, count(t) as numTags\n" + "RETURN p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public static String selectPhotosVertiWithMostTagsFromIDDifferent = "MATCH (p1:Photo{used:true,photoOrientation:'V',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p1.photoId as p1Id, p2.photoId as p2Id, count(t) as numTags\n" + "WHERE p2Id <> MY_OTHER_ID \n"
			+ "RETURN p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public List<String> doTheThing(GraphDatabaseService graphDb) {
		DataAccesService dataAccesService = new DataAccesService();
		List<String> results = new ArrayList<String>();
		results.addAll(doTheThingWithHorizontals(graphDb, dataAccesService));
		results.addAll(doTheThingWithVerticals(graphDb, dataAccesService));
		return results;
	}

	private List<String> doTheThingWithHorizontals(GraphDatabaseService graphDb, DataAccesService dataAccesService) {
		System.err.println("start doTheThingWithHorizontals at " + Instant.now().toString());
		List<String> results = new ArrayList<String>();
		boolean stillDoingIt = true;
		Integer firstPhotoId = dataAccesService.getHorizontalPhotoId(graphDb);
		if (firstPhotoId == null) {
			stillDoingIt = false;
		} else {
			results.add("" + firstPhotoId);
		}

		while (stillDoingIt) {
			Integer secondPhotoId = dataAccesService.getNextHorizontalPhotoId(graphDb, firstPhotoId);
			if (secondPhotoId == null) {
				stillDoingIt = false;
			} else {
				try (Transaction tx = graphDb.beginTx()) {
					results.add("" + secondPhotoId);
					Label photoLabel = Label.label("Photo");
					Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", secondPhotoId);
					secondPhotoNode.setProperty("used", true);
					firstPhotoId = secondPhotoId;
					tx.success();
				}
			}
		}
		System.err.println("end doTheThingWithHorizontals at " + Instant.now().toString());
		return results;
	}

	private List<String> doTheThingWithVerticals(GraphDatabaseService graphDb, DataAccesService dataAccesService) {
		System.err.println("start doTheThingWithVerticals at " + Instant.now().toString());
		List<String> results = new ArrayList<String>();
		boolean stillDoingIt = true;
		Pair<Integer, Integer> ids = dataAccesService.getVerticalPhotoId(graphDb);
		Integer firstSlidePhotoId1 = null;
		Integer firstSlidePhotoId2 = null;
		Integer secondSlidePhotoId1 = null;
		Integer secondSlidePhotoId2 = null;

		if (ids == null) {
			stillDoingIt = false;
		} else {
			firstSlidePhotoId1 = ids.getLeft();
			firstSlidePhotoId2 = ids.getRight();
			results.add("" + firstSlidePhotoId1 + " " + firstSlidePhotoId2);
			try (Transaction tx = graphDb.beginTx()) {
				Label photoLabel = Label.label("Photo");
				Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", firstSlidePhotoId1);
				firstPhotoNode.setProperty("used", true);
				Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", firstSlidePhotoId2);
				secondPhotoNode.setProperty("used", true);
				tx.success();
			}
		}

		while (stillDoingIt) {

			String query1 = StringUtils
					.replaceOnce(StringUtils.replaceOnce(DoTheThingService.selectPhotosVertiWithMostTagsFromID, "MY_ID",
							"" + firstSlidePhotoId1), "MY_OTHER_ID", "" + firstSlidePhotoId2);
			try (Transaction tx = graphDb.beginTx(); Result result1 = graphDb.execute(query1);) {
				if (!result1.hasNext()) {
					stillDoingIt = false;
				}
				if (result1.hasNext()) {
					Map<String, Object> row = result1.next();
					secondSlidePhotoId1 = (Integer) row.get("p2Id");
				}

				tx.success();

			}
			String query2 = StringUtils
					.replaceOnce(StringUtils.replaceOnce(DoTheThingService.selectPhotosVertiWithMostTagsFromIDDifferent,
							"MY_ID", "" + firstSlidePhotoId2), "MY_OTHER_ID", "" + secondSlidePhotoId1);
			try (Transaction tx = graphDb.beginTx(); Result result2 = graphDb.execute(query2);) {
				if (!result2.hasNext()) {
					stillDoingIt = false;
				}
				if (result2.hasNext()) {
					Map<String, Object> row = result2.next();
					secondSlidePhotoId2 = (Integer) row.get("p2Id");
				}

				tx.success();

			}
			try (Transaction tx = graphDb.beginTx()) {

				if (secondSlidePhotoId1 != null && secondSlidePhotoId2 != null) {
					Label photoLabel = Label.label("Photo");
					Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", secondSlidePhotoId1);
					firstPhotoNode.setProperty("used", true);
					Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", secondSlidePhotoId2);
					secondPhotoNode.setProperty("used", true);
					String slide = secondSlidePhotoId1 + " " + secondSlidePhotoId2;
					results.add(slide);
					firstSlidePhotoId1 = secondSlidePhotoId1;
					firstSlidePhotoId2 = secondSlidePhotoId2;
					secondSlidePhotoId1 = null;
					secondSlidePhotoId2 = null;
				}

				tx.success();

			}
		}
		System.err.println("end doTheThingWithVerticals at " + Instant.now().toString());
		return results;
	}

}

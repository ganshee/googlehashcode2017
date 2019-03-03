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

	public static String selectPhotosHorizWithMostTags = "MATCH (p1:Photo{used:false,photoOrientation:'H'})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH p1.photoId as p1Id, p2.photoId as p2Id, count(distinct t) as numTags\n"
			+ "RETURN p1Id, p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public static String selectPhotosHoriz = "MATCH (p:Photo{used:false,photoOrientation:'H'})\n"
			+ "RETURN p.photoId as pId LIMIT 1";

	public static String selectPhotosVertic = "MATCH (p:Photo{used:false,photoOrientation:'V'})\n"
			+ "RETURN p.photoId as pId LIMIT 2";

	public static String selectPhotosHorizWithMostTagsFromID = "MATCH (p1:Photo{used:true,photoOrientation:'H',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH p2.photoId as p2Id, count(distinct t) as numTags\n"
			+ "RETURN p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public static String selectPhotosVertiWithMostTags = "MATCH (p1:Photo{used:true,photoOrientation:'V',photoId:MY_ID})-[:HAS_TAG]->(t1:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'V'})-[:HAS_TAG]->(t2:Tag)<-[:HAS_TAG]-(p3:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p1.photoId as p1Id, p2.photoId as p2Id, count(t) as numTags\n"
			+ "RETURN p1Id, p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public static String selectPhotosVertiWithMostTagsFromID = "MATCH (p1:Photo{used:true,photoOrientation:'V',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p2.photoId as p2Id, count(t) as numTags\n"
			+ "RETURN p2Id, numTags ORDER BY numTags DESC LIMIT 1";
	
	public static String selectPhotosVertiWithMostTagsFromIDDifferent = "MATCH (p1:Photo{used:true,photoOrientation:'V',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p1.photoId as p1Id, p2.photoId as p2Id, count(t) as numTags\n" + "WHERE p2Id <> MY_OTHER_ID \n"
			+ "RETURN p2Id, numTags ORDER BY numTags DESC LIMIT 1";

	public List<String> doTheThing(GraphDatabaseService graphDb) {
		List<String> results = new ArrayList<String>();
		results.addAll(doTheThingWithHorizontals(graphDb));
		results.addAll(doTheThingWithVerticals(graphDb));
		return results;
	}

	private List<String> doTheThingWithHorizontals(GraphDatabaseService graphDb) {
		System.err.println("start doTheThingWithHorizontals at " + Instant.now().toString());
		List<String> results = new ArrayList<String>();
		boolean stillDoingIt = true;
		Integer firstPhotoId = getHorizontalPhotoId(graphDb);
		if (firstPhotoId == null) {
			stillDoingIt = false;
		} else {
			results.add("" + firstPhotoId);
			try (Transaction tx = graphDb.beginTx()) {
				Label photoLabel = Label.label("Photo");
				Node firstPhotoNode = graphDb.findNode(photoLabel, "photoId", firstPhotoId);
				firstPhotoNode.setProperty("used", true);
				tx.success();
			}
		}

		while (stillDoingIt) {
			String query = StringUtils.replaceOnce(DoTheThingService.selectPhotosHorizWithMostTagsFromID, "MY_ID",
					"" + firstPhotoId);
			try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(query)) {
				if (!result.hasNext()) {
					stillDoingIt = false;
				}
				while (result.hasNext()) {
					Map<String, Object> row = result.next();
					Integer secondPhotoId = (Integer) row.get("p2Id");

					results.add("" + secondPhotoId);
					Label photoLabel = Label.label("Photo");
					Node secondPhotoNode = graphDb.findNode(photoLabel, "photoId", secondPhotoId);
					secondPhotoNode.setProperty("used", true);
					firstPhotoId = secondPhotoId;
				}
				tx.success();

			}
		}
		System.err.println("end doTheThingWithHorizontals at " + Instant.now().toString());
		return results;
	}

	private Integer getHorizontalPhotoId(GraphDatabaseService graphDb) {
		System.err.println("start getHorizontalPhotoId at " + Instant.now().toString());
		Integer id = null;
		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(DoTheThingService.selectPhotosHoriz)) {
			if (!result.hasNext()) {
				id = null;
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id = (Integer) row.get("pId");
			}
		}
		return id;
	}

	private List<String> doTheThingWithVerticals(GraphDatabaseService graphDb) {
		System.err.println("start doTheThingWithVerticals at " + Instant.now().toString());
		List<String> results = new ArrayList<String>();
		boolean stillDoingIt = true;
		Pair<Integer, Integer> ids = getVerticalPhotoId(graphDb);
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
			try (Transaction tx = graphDb.beginTx();
					Result result1 = graphDb.execute(query1);){
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
					.replaceOnce(StringUtils.replaceOnce(DoTheThingService.selectPhotosVertiWithMostTagsFromIDDifferent, "MY_ID",
							"" + firstSlidePhotoId2), "MY_OTHER_ID", "" + secondSlidePhotoId1);
			try (Transaction tx = graphDb.beginTx();
					Result result2 = graphDb.execute(query2);){
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

	private Pair<Integer, Integer> getVerticalPhotoId(GraphDatabaseService graphDb) {
		System.err.println("start getVerticalPhotoId at " + Instant.now().toString());
		Integer id1 = null;
		Integer id2 = null;
		try (Transaction tx = graphDb.beginTx();
				Result result = graphDb.execute(DoTheThingService.selectPhotosVertic)) {
			if (!result.hasNext()) {
				id1 = null;
				id2 = null;
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id1 = (Integer) row.get("pId");
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id2 = (Integer) row.get("pId");
			}
		}
		if (id1 != null && id2 != null) {
			return new MutablePair<Integer, Integer>(id1, id2);
		}
		return null;
	}

}

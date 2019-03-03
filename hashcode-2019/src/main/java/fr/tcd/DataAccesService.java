package fr.tcd;

import java.time.Instant;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class DataAccesService {

	public static String countPhotosHoriz = "MATCH (p:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH count(p) as numPhotos LIMIT 1\n"
			+ "RETURN numPhotos";
	
	public static String selectOnePhotosHoriz = "MATCH (p:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH p LIMIT 1\n"
			+ "SET p.used = true\n"
			+ "RETURN p.photoId as result";

	public static String selectPhotosVertic = "MATCH (p:Photo{used:false,photoOrientation:'V'})\n"
			+ "WITH p LIMIT 2\n"
			+ "SET p.used = true\n"
			+ "RETURN p.photoId as result";
	

	public static String selectPhotosHorizWithMostTagsFromID = "MATCH (p1:Photo{used:true,photoOrientation:'H',photoId:MY_ID})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(p2:Photo{used:false,photoOrientation:'H'})\n"
			+ "WITH p2 as p2, count(distinct t) as numTags LIMIT 1\n"
			+ "SET p2.used = true \n"
			+ "RETURN p2.photoId as p2Id, numTags ORDER BY numTags DESC LIMIT 1";
	
	public Integer getHorizontalPhotoId(GraphDatabaseService graphDb) {
		System.err.println("start getHorizontalPhotoId at " + Instant.now().toString());
		Integer id = null;
		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(DataAccesService.selectOnePhotosHoriz)) {
			if (!result.hasNext()) {
				id = null;
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id = (Integer) row.get("result");
			}
			tx.success();
		}
		System.err.println("end getHorizontalPhotoId at " + Instant.now().toString());
		return id;
		
	}
	
	public Pair<Integer,Integer> getVerticalPhotoId(GraphDatabaseService graphDb) {
		System.err.println("start getVerticalPhotoId at " + Instant.now().toString());
		Integer id1 = null;
		Integer id2 = null;
		try (Transaction tx = graphDb.beginTx();
				Result result = graphDb.execute(DataAccesService.selectPhotosVertic)) {
			if (!result.hasNext()) {
				id1 = null;
				id2 = null;
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id1 = (Integer) row.get("result");
			}
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				id2 = (Integer) row.get("result");
			}
			tx.success();
		}
		System.err.println("end getVerticalPhotoId at " + Instant.now().toString());
		if (id1 != null && id2 != null) {
			return new MutablePair<Integer, Integer>(id1, id2);
		}
		return null;
	}
	
	public Integer getNextHorizontalPhotoId(GraphDatabaseService graphDb, Integer photoID) {
		Integer nextPhotoId = null; 
		String query = StringUtils.replaceOnce(DataAccesService.selectPhotosHorizWithMostTagsFromID, "MY_ID",
				"" + photoID);
		try (Transaction tx = graphDb.beginTx(); Result result = graphDb.execute(query)) {
			if (result.hasNext()) {
				Map<String, Object> row = result.next();
				nextPhotoId = (Integer) row.get("p2Id");
			}
			tx.success();
		}
		return nextPhotoId;
	}
}

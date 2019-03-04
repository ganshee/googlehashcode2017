package fr.tcd;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class DoTheThingService {

	public static String updatePhotoAsUsed = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = true \n"
			+ "RETURN p.photoId as pId";

	public static String updatePhotoAsUnused = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = false \n"
			+ "RETURN p.photoId as pId";

	public List<String> doTheThing(GraphDatabaseService graphDb) throws InterruptedException, ExecutionException {
		DataAccesService dataAccesService = new DataAccesService();
		List<String> results = new ArrayList<String>();
		// results.addAll(doTheThingWithHorizontals(graphDb, dataAccesService));
		// results.addAll(doTheThingWithVerticals(graphDb, dataAccesService));

		CompletableFuture<List<String>> horizontal = CompletableFuture
				.supplyAsync(() -> doTheThingWithHorizontals(graphDb, dataAccesService));
		CompletableFuture<List<String>> vertical = CompletableFuture
				.supplyAsync(() -> doTheThingWithVerticals(graphDb, dataAccesService));

		results.addAll(horizontal.get());
		results.addAll(vertical.get());

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

		int counter = 0;
		List<Integer> photos = new ArrayList<Integer>();
		while (stillDoingIt) {
			Integer secondPhotoId = dataAccesService.getNextHorizontalPhotoId(graphDb, firstPhotoId);
			if (secondPhotoId == null) {
				stillDoingIt = false;
			} else {
				results.add(""+secondPhotoId);
				photos.add(firstPhotoId);
				firstPhotoId = secondPhotoId;
			}
			counter++;
			if (counter % 50 == 0) {
				System.out.println("50 horizontal turns more :" + counter + ":" + Instant.now().toString());
				dataAccesService.cleanPhotos(graphDb, photos);
				photos = new ArrayList<Integer>();
			}
		}
		System.out.println(results.size());
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
		}

		int counter = 0;
		List<Integer> photos = new ArrayList<Integer>();
		while (stillDoingIt) {
			secondSlidePhotoId1 = dataAccesService.getNextVerticalPhotoId(graphDb, firstSlidePhotoId1);
			if (secondSlidePhotoId1 == null) {
				stillDoingIt = false;
			} else {
				secondSlidePhotoId2 = dataAccesService.getNextVerticalPhotoIdExceptOtherId(graphDb, firstSlidePhotoId2,
						secondSlidePhotoId1);
				
				results.add("" + secondSlidePhotoId1 + " " + secondSlidePhotoId2);
				photos.add(firstSlidePhotoId1);
				photos.add(firstSlidePhotoId2);
				firstSlidePhotoId1 = secondSlidePhotoId1;
				firstSlidePhotoId2 = secondSlidePhotoId2;
				secondSlidePhotoId1 = null;
				secondSlidePhotoId2 = null;
			}
			counter++;
			if (counter % 50 == 0) {
				System.out.println("50 vertical turns more :" + counter + ":" + Instant.now().toString());
				dataAccesService.cleanPhotos(graphDb, photos);
				photos = new ArrayList<Integer>();
			}
		}
		System.out.println(results.size());
		System.err.println("end doTheThingWithVerticals at " + Instant.now().toString());
		return results;
	}

}

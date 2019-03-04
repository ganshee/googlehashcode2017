package fr.tcd;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.GraphDatabaseService;

public class DoTheThingService {

	public static String updatePhotoAsUsed = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = true \n"
			+ "RETURN p.photoId as pId";

	public static String updatePhotoAsUnused = "MATCH (p:Photo{photoId:MY_ID})\n" + "SET p.used = false \n"
			+ "RETURN p.photoId as pId";

	public DoTheThingResult doTheThing(GraphDatabaseService graphDb)
			throws InterruptedException, ExecutionException, IOException {
		DataAccesService dataAccesService = new DataAccesService();
		DoTheThingResult result = new DoTheThingResult();
		int step = 100;

		DoTheThingResult horizontalResult = workOnHorizontals(graphDb, dataAccesService, step);
		DoTheThingResult verticalResult = workOnVerticals(graphDb, dataAccesService, step);

		result.getFilenames().addAll(horizontalResult.getFilenames());
		result.addToNumResult(horizontalResult.getNumResult());

		result.getFilenames().addAll(verticalResult.getFilenames());
		result.addToNumResult(verticalResult.getNumResult());

		return result;
	}

	private DoTheThingResult workOnHorizontals(GraphDatabaseService graphDb, DataAccesService dataAccesService,
			int step) throws InterruptedException, ExecutionException, IOException {
		DoTheThingResult result = new DoTheThingResult();
		boolean finished = false;
		while (!finished) {
			CompletableFuture<HorizontalComputeState> horizontal = CompletableFuture
					.supplyAsync(() -> doTheThingWithHorizontals(graphDb, dataAccesService, step));
			HorizontalComputeState horizontalResult = horizontal.get();
			result.addToNumResult(horizontalResult.getResults().size());
			result.getFilenames().add(writeInFile(horizontal.get()));
			if (horizontalResult.getResults().size() != step) {
				finished = true;
			}
		}
		return result;
	}

	private DoTheThingResult workOnVerticals(GraphDatabaseService graphDb, DataAccesService dataAccesService, int step)
			throws InterruptedException, ExecutionException, IOException {
		DoTheThingResult result = new DoTheThingResult();
		boolean finished = false;
		while (!finished) {
			CompletableFuture<VerticalComputeState> vertical = CompletableFuture
					.supplyAsync(() -> doTheThingWithVerticals(graphDb, dataAccesService, step));
			VerticalComputeState verticalResult = vertical.get();
			result.addToNumResult(verticalResult.getResults().size());
			result.getFilenames().add(writeInFile(vertical.get()));
			if (verticalResult.getResults().size() != step) {
				finished = true;
			}
		}
		return result;
	}

	private HorizontalComputeState doTheThingWithHorizontals(GraphDatabaseService graphDb,
			DataAccesService dataAccesService, int size) {
		System.err.println("start doTheThingWithHorizontals at " + Instant.now().toString());
		HorizontalComputeState state = new HorizontalComputeState();
		boolean stillDoingIt = true;

		Integer firstPhotoId = dataAccesService.getHorizontalPhotoId(graphDb);
		if (firstPhotoId == null) {
			stillDoingIt = false;
		} else {
			state.setLastPhotoIdUsed(firstPhotoId);
			state.getResults().add(firstPhotoId);
		}

		int counter = 0;
		while (stillDoingIt) {
			Integer secondPhotoId = dataAccesService.getNextHorizontalPhotoId(graphDb, firstPhotoId);
			if (secondPhotoId == null) {
				stillDoingIt = false;
			} else {
				state.setLastPhotoIdUsed(secondPhotoId);
				state.getResults().add(secondPhotoId);
				firstPhotoId = secondPhotoId;
			}
			counter++;
			if (counter == size) {
				System.out.println(size + " horizontal turns more" + Instant.now().toString());
				dataAccesService.cleanPhotos(graphDb, state.getResults());
				stillDoingIt = false;
			}
		}
		System.out.println(state.getResults().size());
		System.err.println("end " + state.getResults() + " doTheThingWithHorizontals at " + Instant.now().toString());
		return state;
	}

	private VerticalComputeState doTheThingWithVerticals(GraphDatabaseService graphDb,
			DataAccesService dataAccesService, int size) {
		System.err.println("start doTheThingWithVerticals at " + Instant.now().toString());
		VerticalComputeState verticalComputeState = new VerticalComputeState();
		boolean stillDoingIt = true;
		Pair<Integer, Integer> firstIds = dataAccesService.getVerticalPhotoId(graphDb);
		Integer secondSlidePhotoId1 = null;
		Integer secondSlidePhotoId2 = null;

		if (firstIds == null) {
			stillDoingIt = false;
		} else {
			verticalComputeState.setLastPhotoIdsUsed(firstIds);
			verticalComputeState.getResults().add(firstIds);
		}

		int counter = 0;
		while (stillDoingIt) {
			secondSlidePhotoId1 = dataAccesService.getNextVerticalPhotoId(graphDb, firstIds.getLeft());
			if (secondSlidePhotoId1 == null) {
				stillDoingIt = false;
			} else {
				secondSlidePhotoId2 = dataAccesService.getNextVerticalPhotoIdExceptOtherId(graphDb, firstIds.getRight(),
						secondSlidePhotoId1);

				Pair<Integer, Integer> secondIds = new ImmutablePair<Integer, Integer>(secondSlidePhotoId1,
						secondSlidePhotoId2);
				verticalComputeState.setLastPhotoIdsUsed(secondIds);
				verticalComputeState.getResults().add(secondIds);
				firstIds = secondIds;
				secondSlidePhotoId1 = null;
				secondSlidePhotoId2 = null;
			}
			counter++;
			if (counter == size) {
				System.out.println(size + " vertical turns more" + Instant.now().toString());
				dataAccesService.cleanPairPhotos(graphDb, verticalComputeState.getResults());
				System.out.println("50 vertical turns more :" + counter + ":" + Instant.now().toString());
			}
		}
		System.out.println(verticalComputeState.getResults().size());
		System.err.println("end doTheThingWithVerticals at " + Instant.now().toString());
		return verticalComputeState;
	}

	private String writeInFile(HorizontalComputeState state) throws IOException {
		String filename = "DTT-H-" + UUID.randomUUID().toString();
		FileUtils.writeLines(new File(filename), state.getResults());
		return filename;
	}

	private String writeInFile(VerticalComputeState state) throws IOException {
		String filename = "DTT-V-" + UUID.randomUUID().toString();
		FileUtils.writeLines(new File(filename), state.getResultLines());
		return filename;
	}

}

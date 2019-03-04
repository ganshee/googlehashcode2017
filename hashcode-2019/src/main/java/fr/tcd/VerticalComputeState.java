package fr.tcd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class VerticalComputeState {

	private final List<Pair<Integer, Integer>> results = new ArrayList<>();
	private Pair<Integer, Integer> lastPhotoIsdUsed = null;

	public List<Pair<Integer, Integer>> getResults() {
		return results;
	}

	public List<String> getResultLines() {
		return results.stream().map(pair -> "" + pair.getLeft() + " " + pair.getRight()).collect(Collectors.toList());
	}

	public Pair<Integer, Integer> getLastPhotoIdsUsed() {
		return lastPhotoIsdUsed;
	}

	public void setLastPhotoIdsUsed(Pair<Integer, Integer> lastPhotoIsdUsed) {
		this.lastPhotoIsdUsed = lastPhotoIsdUsed;
	}
}

package fr.tcd;

import java.util.ArrayList;
import java.util.List;

public class HorizontalComputeState {

	private final List<Integer> results = new ArrayList<Integer>();
	private Integer lastPhotoIdUsed = null;

	public List<Integer> getResults() {
		return results;
	}

	public Integer getLastPhotoIdUsed() {
		return lastPhotoIdUsed;
	}

	public void setLastPhotoIdUsed(Integer lastPhotoIdUsed) {
		this.lastPhotoIdUsed = lastPhotoIdUsed;
	}
}

package fr.tcd;

import java.util.ArrayList;
import java.util.List;

public class DoTheThingResult {
	private List<String> filenames = new ArrayList<String>();
	private int numResult = 0;

	public List<String> getFilenames() {
		return filenames;
	}

	public int getNumResult() {
		return numResult;
	}

	public void addToNumResult(int add) {
		this.numResult += add;
	}
}

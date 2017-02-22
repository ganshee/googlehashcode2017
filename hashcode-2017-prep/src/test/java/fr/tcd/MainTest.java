package fr.tcd;

import org.junit.Test;

import java.io.FileNotFoundException;

public class MainTest {

	@Test
	public void initData() throws FileNotFoundException {
		Main.initData("example.in");
		Main.generateResult();
	}
}
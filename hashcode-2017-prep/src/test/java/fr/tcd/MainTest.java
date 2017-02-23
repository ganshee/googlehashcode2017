package fr.tcd;

import org.junit.Test;

import java.io.IOException;

public class MainTest {

	@Test
	public void initData() throws IOException {
		Main.initData("example.in");
		Main.generateResult();
	}
}
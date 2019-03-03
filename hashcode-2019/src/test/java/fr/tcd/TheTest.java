package fr.tcd;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class TheTest {

	@Test
	public void aTest() throws IOException {
		Main.main(Arrays.asList("a_exemple","a_example.txt").toArray(new String[0]));
	}
	
	@Test
	public void bTest() throws IOException {
		Main.main(Arrays.asList("b_lovely_landscapes","b_lovely_landscapes.txt").toArray(new String[0]));
	}
	
	@Test
	public void cTest() throws IOException {
		Main.main(Arrays.asList("c_memorable_moments","c_memorable_moments.txt").toArray(new String[0]));
	}
	
	@Test
	public void dTest() throws IOException {
		Main.main(Arrays.asList("d_pet_pictures","d_pet_pictures.txt").toArray(new String[0]));
	}
	

	@Test
	public void eTest() throws IOException {
		Main.main(Arrays.asList("e_shiny_selfies","e_shiny_selfies.txt").toArray(new String[0]));
	}
}

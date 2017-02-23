package fr.tcd;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class MainTest {

    @Test
    public void initData() throws IOException {
        Main.main((String[]) Arrays
            .asList("trending_today.in", "me_at_the_zoo.in", "video_worth_spreading.in" , "kittens.in" ).toArray());
    }
}

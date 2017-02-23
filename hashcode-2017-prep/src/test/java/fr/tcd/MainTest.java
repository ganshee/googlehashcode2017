package fr.tcd;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class MainTest {

//    @Test
//    public void initData() throws IOException {
//        Main.main((String[]) Arrays
//                .asList("trending_today.in", "me_at_the_zoo.in", "video_worth_spreading.in", "kittens.in").toArray());
//    }

    @Test
    public void trending_today() throws IOException {
        Main.main(Arrays.asList("trending_today.in").toArray(new String[0]));
    }

    @Test
    public void me_at_the_zoo() throws IOException {
        Main.main(Arrays.asList("me_at_the_zoo.in").toArray(new String[0]));
    }

    @Test
    public void video_worth_spreading() throws IOException {
        Main.main(Arrays.asList("videos_worth_spreading.in").toArray(new String[0]));
    }

    @Test
    public void kittens() throws IOException {
        Main.main(Arrays.asList("kittens.in").toArray(new String[0]));
    }
}

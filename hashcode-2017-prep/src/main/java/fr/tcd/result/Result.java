package fr.tcd.result;

import fr.tcd.Cache;
import fr.tcd.Video;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<Cache> cacheServers = new ArrayList<>();
    public Result(){
        Cache cache1 = new Cache(0);
        Video video1 = new Video();
        video1.id = 42;
        Video video2 = new Video();
        video2.id = 43;
        cache1.videos.add(video1);
        cache1.videos.add(video2);
        Cache cache2 = new Cache(1);

        Cache cache3 = new Cache(2);

        cacheServers.add(cache1);
        cacheServers.add(cache2);
        cacheServers.add(cache3);
    }
}

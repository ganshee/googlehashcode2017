package fr.tcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {

    public final int id;

    public final List<Video> videos = new ArrayList<>();

    public final Map<Endpoint, Integer> endpoints = new HashMap<>();

    public Cache(int id) {
        this.id = id;
    }

    public void addEnPoint(Endpoint endpoint, int latency) {
        endpoints.put(endpoint, latency);
    }

    public int getAvailableSpace(int cacheSize) {
        return cacheSize - videos.stream().mapToInt(video -> video.weight).sum();
    }

}

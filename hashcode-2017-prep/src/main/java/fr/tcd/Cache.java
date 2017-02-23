package fr.tcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    public int id;
    public List<Video> videos = new ArrayList<>();
    public Map<Endpoint, Integer> endpoints = new HashMap<>();

    private int id;

    public Cache(int id) {
        this.id = id;
    }

    public void addEnPoint(Endpoint endpoint, int latency) {
        endpoints.put(endpoint, latency);
    }

    public int getId() {
        return id;
    }
}

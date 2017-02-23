package fr.tcd;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    public Map<Endpoint, Integer> endpoints = new HashMap<>();

    public void addEnPoint(Endpoint endpoint, int latency) {
        endpoints.put(endpoint, latency);
    }
}

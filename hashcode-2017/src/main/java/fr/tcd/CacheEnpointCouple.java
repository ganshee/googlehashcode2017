package fr.tcd;

/**
 * Created by marob on 23/02/17.
 */
public class CacheEnpointCouple {
    public Cache cache;
    public Endpoint endpoint;
    public int latencyGain;
    public CacheEnpointCouple(Cache cache, Endpoint endpoint, int cacheEnpointLatency) {
        this.cache = cache;
        this.endpoint = endpoint;
        this.latencyGain = endpoint.datacenterLatency - cacheEnpointLatency;
    }
}

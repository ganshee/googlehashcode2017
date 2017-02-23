package fr.tcd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pallain - 2/23/17.
 */
public class InputData {

    public final int nbVideos;

    public final int nbEndpoints;

    public final int nbRequestDescriptions;

    public final int nbCaches;

    public final int cacheSize;

    public final List<Video> videos;

    public final List<Endpoint> endpoints;

    public final List<Request> requests;

    public final List<Cache> caches;

    public InputData(final int nbVideos, final int nbEndpoints, final int nbRequestDescriptions,
                     final int nbCaches, final int cacheSize,
                     List<Video> videos, List<Cache> caches, List<Endpoint> endpoints, List<Request> requests) {
        this.nbVideos = nbVideos;
        this.nbEndpoints = nbEndpoints;
        this.nbRequestDescriptions = nbRequestDescriptions;
        this.nbCaches = nbCaches;
        this.cacheSize = cacheSize;
        this.videos = new ArrayList<>(videos);
        this.caches = new ArrayList<>(caches);
        this.endpoints = new ArrayList<>(endpoints);
        this.requests = new ArrayList<>(requests);
    }

    public int getNbVideos() {
        return nbVideos;
    }

    public int getNbEndpoints() {
        return nbEndpoints;
    }

    public int getNbRequestDescriptions() {
        return nbRequestDescriptions;
    }

    public int getNbCaches() {
        return nbCaches;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    @Override
    public String toString() {
        return "InputData{" +
                "nbVideos=" + nbVideos +
                ", nbEndpoints=" + nbEndpoints +
                ", nbRequestDescriptions=" + nbRequestDescriptions +
                ", nbCaches=" + nbCaches +
                ", cacheSize=" + cacheSize +
                ", videos=" + videos +
                ", endpoints=" + endpoints +
                ", requests=" + requests +
                ", caches=" + caches +
                '}';
    }
}

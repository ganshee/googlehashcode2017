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

    public List<Video> videos = new ArrayList<>();

    public List<Endpoint> endpoints = new ArrayList<>();

    public List<Request> requests = new ArrayList<>();

    public InputData(final int nbVideos, final int nbEndpoints, final int nbRequestDescriptions, final int nbCaches, final int cacheSize, List<Video> videos, List<Cache> caches, List<Endpoint> endpoints) {
        this.nbVideos = nbVideos;
        this.nbEndpoints = nbEndpoints;
        this.nbRequestDescriptions = nbRequestDescriptions;
        this.nbCaches = nbCaches;
        this.cacheSize = cacheSize;
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
}

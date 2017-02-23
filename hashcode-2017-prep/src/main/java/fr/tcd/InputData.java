package fr.tcd;

import java.util.List;

/**
 * @author pallain - 2/23/17.
 */
public class InputData {

    private final int nbVideos;

    private final int nbEndpoints;

    private final int nbRequestDescriptions;

    private final int nbCaches;

    private final int cacheSize;

    private List<Video> videos;

    private List<Endpoint> endpoints;

    private List<Request> requests;

    public InputData(final int nbVideos, final int nbEndpoints, final int nbRequestDescriptions, final int nbCaches, final int cacheSize, List<Video> videos, List<Cache> caches) {
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

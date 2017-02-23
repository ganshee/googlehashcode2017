package fr.tcd;

public class Request {
    public int id;
    public Video video;
    public int nbRequest;
    public Endpoint endpoint;

    /**
     * Sets the video
     * 
     * @param video the video to set
     *
     * @return this
     */
    public Request setVideo(Video video) {
        this.video = video;
        return this;
    }

    /**
     * Sets the nbRequest
     * 
     * @param nbRequest the nbRequest to set
     *
     * @return this
     */
    public Request setNbRequest(int nbRequest) {
        this.nbRequest = nbRequest;
        return this;
    }

    /**
     * Sets the endpoint
     * 
     * @param endpoint the endpoint to set
     *
     * @return this
     */
    public Request setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        return this;
    }



}

package fr.tcd;

public class Endpoint {
    public int id;
    public int latency;

    /**
     * Sets the id
     * 
     * @param id the id to set
     *
     * @return this
     */
    public Endpoint setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the latency
     * 
     * @param latency the latency to set
     *
     * @return this
     */
    public Endpoint setLatency(int latency) {
        this.latency = latency;
        return this;
    }

}

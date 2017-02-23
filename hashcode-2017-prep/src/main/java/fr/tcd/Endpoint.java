package fr.tcd;

import java.util.Objects;

public class Endpoint {

    public int id;

    public int latency;

    /**
     * Sets the id
     *
     * @param id the id to set
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
     * @return this
     */
    public Endpoint setLatency(int latency) {
        this.latency = latency;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return id == endpoint.id &&
                latency == endpoint.latency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latency);
    }
}

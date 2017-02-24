package fr.tcd;

import java.util.Objects;

public class Endpoint {

    public int id;

    public int datacenterLatency;

    public int numberConnectedCaches;

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
     * Sets the datacenterLatency
     *
     * @param datacenterLatency the datacenterLatency to set
     * @return this
     */
    public Endpoint setDatacenterLatency(int datacenterLatency) {
        this.datacenterLatency = datacenterLatency;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return id == endpoint.id &&
                datacenterLatency == endpoint.datacenterLatency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datacenterLatency);
    }

    public Endpoint setNumberConnectedCaches(int numberConnectedCaches) {
        this.numberConnectedCaches = numberConnectedCaches;
        return this;
    }
}

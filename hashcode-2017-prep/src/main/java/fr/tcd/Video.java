package fr.tcd;

public class Video {
    public int id;

    public int weight;

    public Video setWeight(int weight) {
        this.weight = weight;
        return this;
    }
    
    public Video setId(int id) {
        this.id = id;
        return this;
    }
}

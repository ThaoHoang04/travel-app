package com.example.travelapp.Domain;

public class RatingModel {
    private String name;
    private float rating;
    private String comment;

    public RatingModel(String name, float rating, String comment) {
        this.name = name;
        this.rating = rating;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}

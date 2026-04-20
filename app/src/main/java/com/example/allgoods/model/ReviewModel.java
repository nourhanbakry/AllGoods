package com.example.allgoods.model;

public class ReviewModel {
    private String reviewerName;
    private String reviewDate;
    private float rating;
    private String description;
    private String reviewerImage; // URL or Resource name

    public ReviewModel(String reviewerName, String reviewDate, float rating, String description) {
        this.reviewerName = reviewerName;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.description = description;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public float getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }
}

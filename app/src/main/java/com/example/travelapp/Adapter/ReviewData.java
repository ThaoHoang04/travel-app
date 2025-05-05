package com.example.travelapp.Adapter;
public class ReviewData {
    private String orderId;
    private String ratingValue;
    private String comment;
    private String name;
    private String email;
    private String reviewId;
    private String created;

    public ReviewData() {}

    public ReviewData(String orderId, String ratingValue, String comment,
                      String name, String email, String reviewId, String created) {
        this.orderId = orderId;
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.name = name;
        this.email = email;
        this.reviewId = reviewId;
        this.created = created;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}





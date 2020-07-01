package com.beforevisit.beforevisitapp.Model;

public class RatingAndReviews {
    String doc_id;
    float rating;
    String review;
    String date_created;
    String user_name;

    public RatingAndReviews(String doc_id, float rating, String review, String date_created, String user_name) {
        this.doc_id = doc_id;
        this.rating = rating;
        this.review = review;
        this.date_created = date_created;
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}

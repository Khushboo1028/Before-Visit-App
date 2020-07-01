package com.beforevisit.beforevisitapp.Model;

public class SearchResults {

    String doc_id;
    String place_name;
    String address;
    String keywords;
    String image_url;
    float rating;

    public SearchResults(String doc_id, String place_name, String address, String keywords, String image_url, float rating) {
        this.doc_id = doc_id;
        this.place_name = place_name;
        this.address = address;
        this.keywords = keywords;
        this.image_url = image_url;
        this.rating = rating;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

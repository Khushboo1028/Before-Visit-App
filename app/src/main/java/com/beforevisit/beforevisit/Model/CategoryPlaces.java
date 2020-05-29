package com.beforevisit.beforevisit.Model;

public class CategoryPlaces {
    String store_name;
    String address;
    String image_url;
    float rating;
    Boolean isSaved;
    String place_id;

    public CategoryPlaces(String store_name, String address, String image_url, float rating, Boolean isSaved, String place_id) {
        this.store_name = store_name;
        this.address = address;
        this.image_url = image_url;
        this.rating = rating;
        this.isSaved = isSaved;
        this.place_id = place_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public Boolean getSaved() {
        return isSaved;
    }

    public void setSaved(Boolean saved) {
        isSaved = saved;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}

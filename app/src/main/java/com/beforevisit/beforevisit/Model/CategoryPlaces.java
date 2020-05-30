package com.beforevisit.beforevisit.Model;

import java.util.Comparator;

public class CategoryPlaces {
    String store_name;
    String address;
    String image_url;
    float rating;
    Boolean isSaved;
    String place_id;
    double latitude;
    double longitude;
    String distance;

    public CategoryPlaces(String store_name, String address, String image_url, float rating, Boolean isSaved, String place_id, double latitude, double longitude, String distance) {
        this.store_name = store_name;
        this.address = address;
        this.image_url = image_url;
        this.rating = rating;
        this.isSaved = isSaved;
        this.place_id = place_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public static Comparator<CategoryPlaces> distanceLowToHigh =new Comparator<CategoryPlaces>() {
        @Override
        public int compare(CategoryPlaces o1, CategoryPlaces o2) {
            return (Integer.parseInt(o2.getDistance())<Integer.parseInt(o1.getDistance()) ? -1:
                    (Integer.parseInt(o2.getDistance())==Integer.parseInt(o1.getDistance()) ? 0 : 1));
        }
    };

}

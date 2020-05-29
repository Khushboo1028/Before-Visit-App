package com.beforevisit.beforevisit.Model;

import java.util.ArrayList;

public class Places {

    String doc_id;
    String about_Store;
    String place_address;
    String category_id;
    String home_image_url;
    ArrayList<String> images_url;
    Boolean is_offering_promo;
    Boolean is_sponsored;
    ArrayList<String> mobile_no_array;
    String place_name;
    String offer_image_url;
    long saved_count;
    String search_keywords;
    String video_url;
    long visitor_count;
    int avg_rating;

    public Places(String doc_id, String about_Store, String place_address, String category_id, String home_image_url, ArrayList<String> images_url, Boolean is_offering_promo, Boolean is_sponsored, ArrayList<String> mobile_no_array, String place_name, String offer_image_url, long saved_count, String search_keywords, String video_url, long visitor_count, int avg_rating) {
        this.doc_id = doc_id;
        this.about_Store = about_Store;
        this.place_address = place_address;
        this.category_id = category_id;
        this.home_image_url = home_image_url;
        this.images_url = images_url;
        this.is_offering_promo = is_offering_promo;
        this.is_sponsored = is_sponsored;
        this.mobile_no_array = mobile_no_array;
        this.place_name = place_name;
        this.offer_image_url = offer_image_url;
        this.saved_count = saved_count;
        this.search_keywords = search_keywords;
        this.video_url = video_url;
        this.visitor_count = visitor_count;
        this.avg_rating = avg_rating;
    }

    public String getHome_image_url() {
        return home_image_url;
    }

    public void setHome_image_url(String home_image_url) {
        this.home_image_url = home_image_url;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getAbout_Store() {
        return about_Store;
    }

    public void setAbout_Store(String about_Store) {
        this.about_Store = about_Store;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public ArrayList<String> getImages_url() {
        return images_url;
    }

    public void setImages_url(ArrayList<String> images_url) {
        this.images_url = images_url;
    }

    public Boolean getIs_offering_promo() {
        return is_offering_promo;
    }

    public void setIs_offering_promo(Boolean is_offering_promo) {
        this.is_offering_promo = is_offering_promo;
    }

    public Boolean getIs_sponsored() {
        return is_sponsored;
    }

    public void setIs_sponsored(Boolean is_sponsored) {
        this.is_sponsored = is_sponsored;
    }

    public ArrayList<String> getMobile_no_array() {
        return mobile_no_array;
    }

    public void setMobile_no_array(ArrayList<String> mobile_no_array) {
        this.mobile_no_array = mobile_no_array;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getOffer_image_url() {
        return offer_image_url;
    }

    public void setOffer_image_url(String offer_image_url) {
        this.offer_image_url = offer_image_url;
    }

    public long getSaved_count() {
        return saved_count;
    }

    public void setSaved_count(long saved_count) {
        this.saved_count = saved_count;
    }

    public String getSearch_keywords() {
        return search_keywords;
    }

    public void setSearch_keywords(String search_keywords) {
        this.search_keywords = search_keywords;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public long getVisitor_count() {
        return visitor_count;
    }

    public void setVisitor_count(long visitor_count) {
        this.visitor_count = visitor_count;
    }

    public int getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(int avg_rating) {
        this.avg_rating = avg_rating;
    }
}

package com.beforevisit.beforevisitapp.Model;

import java.util.ArrayList;

public class PlacesHomePage {
    String doc_id;
    String offer_image_url;

    public PlacesHomePage(String doc_id, String offer_image_url) {
        this.doc_id = doc_id;
        this.offer_image_url = offer_image_url;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getOffer_image_url() {
        return offer_image_url;
    }

    public void setOffer_image_url(String offer_image_url) {
        this.offer_image_url = offer_image_url;
    }
}

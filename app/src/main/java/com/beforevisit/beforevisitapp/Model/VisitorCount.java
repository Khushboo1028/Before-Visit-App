package com.beforevisit.beforevisitapp.Model;

public class VisitorCount {

    String place_id;
    long visitor_count;

    public VisitorCount(String place_id, long visitor_count) {
        this.place_id = place_id;
        this.visitor_count = visitor_count;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public long getVisitor_count() {
        return visitor_count;
    }

    public void setVisitor_count(long visitor_count) {
        this.visitor_count = visitor_count;
    }
}

package com.beforevisit.beforevisitapp.Model;

import java.util.Comparator;

public class Location {
    String doc_id;
    double distance;

    public Location(String doc_id, double distance) {
        this.doc_id = doc_id;
        this.distance = distance;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static Comparator<Location> distanceLowToHigh =new Comparator<Location>() {
        @Override
        public int compare(Location o1, Location o2) {
            return (o2.getDistance()<o1.getDistance() ? -1:
                    (o2.getDistance()==o1.getDistance() ? 0 : 1));
        }
    };

}

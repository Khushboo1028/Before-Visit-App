package com.beforevisit.beforevisitapp.Utility;

import com.beforevisit.beforevisitapp.Model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingClass {

    List<Location> sortingClass = new ArrayList<>();

    public SortingClass(List<Location> distanceSort) {
        this.sortingClass = distanceSort;
    }
    public List<Location> sortDistanceLowToHigh() {
        Collections.sort(sortingClass, Location.distanceLowToHigh);
        return sortingClass;
    }


}

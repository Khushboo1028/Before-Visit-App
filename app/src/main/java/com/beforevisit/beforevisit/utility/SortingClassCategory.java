package com.beforevisit.beforevisit.utility;

import com.beforevisit.beforevisit.Model.CategoryPlaces;
import com.beforevisit.beforevisit.Model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingClassCategory {


    List<CategoryPlaces> sortingClassCategory = new ArrayList<>();

    public SortingClassCategory(List<CategoryPlaces> distanceSort) {
        this.sortingClassCategory = distanceSort;
    }
    public List<CategoryPlaces> sortDistanceLowToHigh() {
        Collections.sort(sortingClassCategory, CategoryPlaces.distanceLowToHigh);
        return sortingClassCategory;
    }


}

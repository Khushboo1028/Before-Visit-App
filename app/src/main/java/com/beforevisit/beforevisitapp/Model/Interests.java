package com.beforevisit.beforevisitapp.Model;

public class Interests {

    String interest_id;
    String name;

    public Interests(String interest_id, String name) {
        this.interest_id = interest_id;
        this.name = name;
    }

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

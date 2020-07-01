package com.beforevisit.beforevisitapp.Model;

public class Category {

    String icon_name;
    String icon_url;
    String category_id;

    public Category(String icon_name, String icon_url, String category_id) {
        this.icon_name = icon_name;
        this.icon_url = icon_url;
        this.category_id = category_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}

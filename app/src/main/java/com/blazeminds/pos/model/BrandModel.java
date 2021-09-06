package com.blazeminds.pos.model;

public class BrandModel {
    private String id,name;

    private boolean isExpanded;

    public BrandModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public BrandModel(String id, String name, boolean isExpanded) {
        this.id = id;
        this.name = name;
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

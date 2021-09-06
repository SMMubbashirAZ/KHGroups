package com.blazeminds.pos.model;

import java.util.ArrayList;

public class MainCartModel {

    private String b_id,b_name;

    private boolean isExpanded;
    private ArrayList<ProductModel> productList;

    public MainCartModel() {
    }

    public MainCartModel(String b_id, String b_name, ArrayList<ProductModel> productList) {
        this.b_id = b_id;
        this.b_name = b_name;
        this.productList = productList;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public ArrayList<ProductModel> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<ProductModel> productList) {
        this.productList = productList;
    }
}

package com.gm.blackkite.model;

import java.util.ArrayList;

public class OfflineCart {

    String id;
    String product_id;
    String type;
    String measurement;
    String price;
    String discounted_price;
    String serve_for;
    String stock;
    String product_variant_id;
    ArrayList<OfflineItems> item;

    public String getProduct_variant_id() {
        return product_variant_id;
    }

    public String getStock() {
        return stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }


    public String getServe_for() {
        return serve_for;
    }

    public ArrayList<OfflineItems> getItem() {
        return item;
    }

    public void setItem(ArrayList<OfflineItems> item) {
        this.item = item;
    }
}

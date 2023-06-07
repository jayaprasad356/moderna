package com.gm.blackkite.model;

import java.io.Serializable;

public class PriceVariation implements Serializable {
    String id;
    String product_id;
    String type;
    String measurement;
    String price;
    String discounted_price;
    String serve_for;
    String stock;
    String measurement_unit_name;
    String cart_count;

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

    public String getStock() {
        return stock;
    }

    public String getMeasurement_unit_name() {
        return measurement_unit_name;
    }

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }
}

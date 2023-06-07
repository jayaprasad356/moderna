package com.gm.blackkite.model;

import java.io.Serializable;

public class CartItems implements Serializable {
    String measurement;
    String price;
    String discounted_price;
    String serve_for;
    String stock;
    String name;
    String cod_allowed;
    String image;
    String tax_percentage;
    String tax_title;
    String unit;
    String is_item_deliverable;

    public String getMeasurement() {
        return measurement;
    }

    public String getPrice() {
        return price;
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

    public String getName() {
        return name;
    }

    public String getCod_allowed() {
        return cod_allowed;
    }

    public String getImage() {
        return image;
    }

    public String getTax_percentage() {
        return tax_percentage;
    }

    public String getTax_title() {
        return tax_title;
    }

    public String getUnit() {
        return unit;
    }

    public String getIs_item_deliverable() {
        return is_item_deliverable;
    }
}

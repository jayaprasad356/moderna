package com.gm.blackkite.model;

import java.util.ArrayList;

public class Cart {

    String product_id;
    String product_variant_id;
    String qty;
    String save_for_later;
    ArrayList<CartItems> item;

    public String getSave_for_later() {
        return save_for_later;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_variant_id() {
        return product_variant_id;
    }


    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public ArrayList<CartItems> getItems() {
        return item;
    }

    public void setItems(ArrayList<CartItems> items) {
        this.item = items;
    }
}

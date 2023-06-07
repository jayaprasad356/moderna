package com.gm.blackkite.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    String id;
    String seller_id;
    String name;
    String slug;
    String category_id;
    String indicator;
    String manufacturer;
    String made_in;
    String return_status;
    String cancelable_status;
    String till_status;
    String image;
    String description;
    String status;
    String return_days;
    String type;
    String seller_name;
    String tax_percentage;
    String total_allowed_quantity;
    ArrayList<PriceVariation> variants;
    ArrayList<String> other_images;
    boolean is_favorite;

    public String getTotal_allowed_quantity() {
        return total_allowed_quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getIndicator() {
        return indicator;
    }


    public String getManufacturer() {
        return manufacturer;
    }

    public String getMade_in() {
        return made_in;
    }

    public String getReturn_status() {
        return return_status;
    }

    public String getCancelable_status() {
        return cancelable_status;
    }

    public String getTill_status() {
        return till_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReturn_days() {
        return return_days;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getTax_percentage() {
        return tax_percentage;
    }

    public ArrayList<PriceVariation> getPriceVariations() {
        return variants;
    }

    public ArrayList<String> getOther_images() {
        return other_images;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }
}

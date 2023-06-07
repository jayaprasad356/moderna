package com.gm.blackkite.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderTracker implements Serializable {

    public String otp;
    public String return_status;
    public String cancelable_status;
    public String till_status;
    public String return_days;

    public String username;
    public String id;
    public String user_id;
    public String order_id;
    public String product_variant_id;
    public String quantity;
    public String price;
    public String discount;
    public String dPercent;
    public String dAmount;
    public String sub_total;
    public String tax_amt;
    public String tax_percent;
    public String date_added;
    public String name;
    public String image;
    public String measurement;
    public String unit;
    public String status;
    public String statusdate;
    public String mobile;
    public String delivery_charge;
    public String payment_method;
    public String address;
    public String final_total;
    public String total;
    public String walletBalance;
    public String promoCode;
    public String promoDiscount;
    public String activeStatus;
    public String activeStatusDate;
    public String discounted_price;
    public ArrayList<OrderTracker> orderStatusArrayList;
    public ArrayList<OrderTracker> itemsList;


    public OrderTracker(String id, String otp, String user_id, String date_added, String mobile, String delivery_charge, String payment_method, String address, String total, String final_total, String tax_amt, String tax_percent, String walletBalance, String promoCode, String promoDiscount, String dPercent, String dAmount, String username, ArrayList<OrderTracker> itemsList) {
        this.id = id;
        this.otp = otp;
        this.user_id = user_id;
        this.date_added = date_added;
        this.mobile = mobile;
        this.delivery_charge = delivery_charge;
        this.payment_method = payment_method;
        this.address = address;
        this.total = total;
        this.final_total = final_total;
        this.tax_amt = tax_amt;
        this.tax_percent = tax_percent;
        this.walletBalance = walletBalance;
        this.promoCode = promoCode;
        this.promoDiscount = promoDiscount;
        this.dAmount = dAmount;
        this.dPercent = dPercent;
        this.username = username;
        this.itemsList = itemsList;
    }

    public OrderTracker(String id, String order_id, String user_id, String product_variant_id, String quantity, String price, String discount, String sub_total, String name, String image, String measurement, String unit, String payment_method, String activeStatus, String activeStatusDate, ArrayList<OrderTracker> orderStatusArrayList, String return_status, String return_days, String cancelable_status, String till_status, String discounted_price, String tax_percent) {
        this.id = id;
        this.order_id = order_id;
        this.user_id = user_id;
        this.product_variant_id = product_variant_id;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.sub_total = sub_total;
        this.name = name;
        this.image = image;
        this.measurement = measurement;
        this.unit = unit;
        this.payment_method = payment_method;
        this.activeStatus = activeStatus;
        this.activeStatusDate = activeStatusDate;
        this.orderStatusArrayList = orderStatusArrayList;
        this.return_status = return_status;
        this.cancelable_status = cancelable_status;
        this.till_status = till_status;
        this.return_days = return_days;
        this.discounted_price = discounted_price;
        this.tax_percent = tax_percent;
    }


    public OrderTracker(String status, String statusdate) {
        this.status = status;
        this.statusdate = statusdate;
    }

    public String getReturn_days() {
        return return_days;
    }

    public String getOtp() {
        return otp;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getdPercent() {
        return dPercent;
    }

    public String getdAmount() {
        return dAmount;
    }

    public String getTax_percent() {
        return tax_percent;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusdate() {
        return statusdate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFinal_total() {
        return final_total;
    }

    public void setFinal_total(String final_total) {
        this.final_total = final_total;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public String getPromoDiscount() {
        return promoDiscount;
    }

    public String getActiveStatusDate() {
        return activeStatusDate;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public ArrayList<OrderTracker> getOrderStatusArrayList() {
        return orderStatusArrayList;
    }

    public ArrayList<OrderTracker> getItemsList() {
        return itemsList;
    }
}
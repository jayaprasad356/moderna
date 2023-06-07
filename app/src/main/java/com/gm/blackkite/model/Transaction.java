package com.gm.blackkite.model;

public class Transaction {

    String id;
    String order_id;
    String type;
    String txn_id;
    String amount;
    String status;
    String message;
    String date_created;

    public String getId() {
        return id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getType() {
        return type;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDate_created() {
        return date_created;
    }
}

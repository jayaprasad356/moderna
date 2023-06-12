package com.gm.moderna.helper;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.gm.moderna.R;
import com.gm.moderna.activity.MainActivity;
import com.gm.moderna.fragment.CheckoutFragment;
import com.gm.moderna.fragment.WalletTransactionFragment;

public class PaymentModelClass {
    public final Activity activity;
    final String TAG = CheckoutFragment.class.getSimpleName();
    //public PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    public String status, udf5, udf4, udf3, udf2, udf1, email, firstName, productInfo, amount, txnId, key, addedOn, msg, Product, address;
    public static Map<String, String> sendParams;
    ProgressDialog mProgressDialog;

    public PaymentModelClass(Activity activity) {
        this.activity = activity;
    }

    //this method calculate hash from sdk
    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] bytes = messageDigest.digest();
            for (byte hashByte : bytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }

    public static String hashCal1(String str) {
        byte[] hashsets = str.getBytes();
        StringBuilder hexString = new StringBuilder();

        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashsets);
            byte[] messageDigest = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(activity.getString(R.string.processing));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void PlaceOrder(final Activity activity, final String paymentType, final String txnid, boolean isSuccess, final Map<String, String> sendParams, final String status) {
        showProgressDialog();
        if (isSuccess) {
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            activity.finish();
                            AddTransaction(object.getString(Constant.ORDER_ID), paymentType, txnid, status, activity.getResources().getString(R.string.order_success), sendParams);
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constant.FROM, "payment_success");
                            activity.startActivity(intent);
                        } else {
                            hideProgressDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();

                    }
                }
            }, activity, Constant.ORDER_PROCESS_URL, sendParams, false);
        } else {
            hideProgressDialog();
            AddTransaction("", activity.getResources().getString(R.string.payu_money_type), txnid, status, "Order Failed", sendParams);
        }
    }

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendParams) {
        Map<String, String> transactionParams = new HashMap<>();
        transactionParams.put(Constant.ADD_TRANSACTION, Constant.GetVal);
        transactionParams.put(Constant.USER_ID, sendParams.get(Constant.USER_ID));
        transactionParams.put(Constant.ORDER_ID, orderId);
        transactionParams.put(Constant.TYPE, paymentType);
        transactionParams.put(Constant.TRANS_ID, txnid);
        transactionParams.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        transactionParams.put(Constant.STATUS, status);
        transactionParams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transactionParams.put("transaction_date", df.format(c));
        ApiConfig.RequestToVolley((result, response) -> {
            //System.out.println ("=================*transaction- " + response);
            if (result) {
                try {
                    hideProgressDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, activity, Constant.ORDER_PROCESS_URL, transactionParams, false);
    }
}
package com.gm.moderna.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.gm.moderna.R;
import com.gm.moderna.fragment.AddressListFragment;
import com.gm.moderna.helper.ApiConfig;
import com.gm.moderna.helper.Constant;
import com.gm.moderna.helper.Session;

public class StripeActivity extends AppCompatActivity {

    boolean isTxnInProcess = true;
    Button payButton;
    Map<String, String> sendParams;
    Session session;
    Toolbar toolbar;
    TextView tvTitle, tvPayableAmount;
    String amount;
    private Stripe stripe;
    private String paymentIntentClientSecret, stripePublishableKey, orderId, from;

    CardView cardViewHamburger;
    TextView toolbarTitle;
    ImageView imageMenu;
    ImageView imageHome;
    Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        activity = this;
        session = new Session(activity);
        sendParams = (Map<String, String>) getIntent().getSerializableExtra(Constant.PARAMS);
        orderId = getIntent().getStringExtra(Constant.ORDER_ID);
        from = getIntent().getStringExtra(Constant.FROM);
        amount = sendParams.get(Constant.FINAL_TOTAL);


        cardViewHamburger = findViewById(R.id.cardViewHamburger);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imageMenu = findViewById(R.id.imageMenu);
        imageHome = findViewById(R.id.imageHome);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        toolbarTitle.setText(getString(R.string.payment));

        imageHome.setVisibility(View.GONE);

        imageMenu.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_arrow_back));
        imageMenu.setVisibility(View.VISIBLE);

        cardViewHamburger.setOnClickListener(view -> onBackPressed());

        payButton = findViewById(R.id.payButton);
        tvTitle = findViewById(R.id.tvTitle);
        tvPayableAmount = findViewById(R.id.tvPayableAmount);

        if (from.equals(Constant.PAYMENT)) {
            tvTitle.setText(getString(R.string.app_name) + getString(R.string.shopping));
        } else {
            tvTitle.setText(getString(R.string.app_name) + getString(R.string.wallet_recharge_));
        }
        tvPayableAmount.setText(session.getData(Constant.CURRENCY) + sendParams.get(Constant.FINAL_TOTAL));

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.stripe));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startCheckout();
    }

    private void startCheckout() {
        String address = null;

        if (from.equals(Constant.PAYMENT)) {
            address = AddressListFragment.selectedAddress;
        } else if (from.equals(Constant.WALLET)) {
            address = Constant.DefaultAddress;
        }

        Map<String, String> params = new HashMap<>();
        params.put(Constant.NAME, session.getData(Constant.NAME));
        params.put(Constant.ADDRESS_LINE1, address);
        if (Constant.DefaultPinCode.length() > 5) {
            params.put(Constant.POSTAL_CODE, "" + (Integer.parseInt(Constant.DefaultPinCode) / 10));
        } else {
            params.put(Constant.POSTAL_CODE, "" + Constant.DefaultPinCode);
        }
        params.put(Constant.CITY, Constant.DefaultCity);
        params.put(Constant.AMOUNT, amount);
        params.put(Constant.ORDER_ID, orderId);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        stripePublishableKey = jsonObject.getString(Constant.publishableKey);
                        paymentIntentClientSecret = jsonObject.getString(Constant.clientSecret);

                        stripe = new Stripe(
                                activity,
                                Objects.requireNonNull(stripePublishableKey)
                        );

                        payButton.setOnClickListener((View view) -> {
                            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                            PaymentMethodCreateParams params1 = cardInputWidget.getPaymentMethodCreateParams();
                            if (params1 != null) {
                                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                        .createWithPaymentMethodCreateParams(params1, paymentIntentClientSecret);
                                stripe.confirmPayment(StripeActivity.this, confirmParams);
                            }
                        });
                    } else {
                        payButton.setEnabled(false);
                        Toast.makeText(StripeActivity.this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                        e.printStackTrace();
                }
            }
        }, StripeActivity.this, Constant.STRIPE_BASE_URL, params, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isTxnInProcess = false;
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    public void AddTransaction(Activity activity, String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendParams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.ADD_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendParams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put("transaction_date", df.format(c));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {

                        if (from.equals(Constant.WALLET)) {
                            onBackPressed();
                            ApiConfig.getWalletBalance(activity, session);
                            Toast.makeText(activity, activity.getString(R.string.wallet_message), Toast.LENGTH_SHORT).show();
                        } else if (from.equals(Constant.PAYMENT)) {
                            if (status.equals(Constant.SUCCESS) || status.equals(Constant.AWAITING_PAYMENT)) {
                                finish();
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Constant.FROM, "payment_success");
                                activity.startActivity(intent);
                            } else {
                                finish();
                            }
                        }
                    }
                } catch (JSONException e) {
                        e.printStackTrace();

                }
            }
        }, activity, Constant.ORDER_PROCESS_URL, transparams, true);
    }

    @Override
    public void onBackPressed() {
        if (isTxnInProcess)
            ProcessAlertDialog();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void ProcessAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StripeActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.txn_cancel_msg));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            alertDialog1.dismiss();
            DeleteTransaction(StripeActivity.this, orderId);
        }).setNegativeButton(getString(R.string.no), (dialog, which) -> alertDialog1.dismiss());

        alertDialog.show();
    }

    public void DeleteTransaction(Activity activity, String orderId) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.DELETE_ORDER, Constant.GetVal);
        transparams.put(Constant.ORDER_ID, orderId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    PaymentActivity.dialog_.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StripeActivity.super.onBackPressed();
            }
        }, activity, Constant.ORDER_PROCESS_URL, transparams, false);
    }

    private final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {

        PaymentResultCallback(@NonNull StripeActivity activity) {
            WeakReference<StripeActivity> activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                AddTransaction(StripeActivity.this, orderId, getString(R.string.stripe), orderId, Constant.SUCCESS, "", sendParams);
            } else if (status == PaymentIntent.Status.Processing) {
                AddTransaction(StripeActivity.this, orderId, getString(R.string.stripe), orderId, Constant.AWAITING_PAYMENT, "", sendParams);
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            DeleteTransaction(StripeActivity.this, orderId);
            Toast.makeText(StripeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

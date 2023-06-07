package com.gm.blackkite.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gm.blackkite.R;
import com.gm.blackkite.activity.LoginActivity;
import com.gm.blackkite.activity.MainActivity;
import com.gm.blackkite.adapter.CartAdapter;
import com.gm.blackkite.adapter.OfflineCartAdapter;
import com.gm.blackkite.adapter.OfflineSaveForLaterAdapter;
import com.gm.blackkite.adapter.SaveForLaterAdapter;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.DatabaseHelper;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.Cart;
import com.gm.blackkite.model.OfflineCart;

@SuppressLint("SetTextI18n")
public class CartFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout lytEmpty;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout lytTotal;
    public static ArrayList<Cart> carts, saveForLater;
    public static ArrayList<OfflineCart> offlineCarts, offlineSaveForLaterItems;
    @SuppressLint("StaticFieldLeak")
    public static CartAdapter cartAdapter;
    @SuppressLint("StaticFieldLeak")
    public static SaveForLaterAdapter saveForLaterAdapter;
    @SuppressLint("StaticFieldLeak")
    public static OfflineCartAdapter offlineCartAdapter;
    @SuppressLint("StaticFieldLeak")
    public static OfflineSaveForLaterAdapter offlineSaveForLaterAdapter;
    public static HashMap<String, String> values, saveForLaterValues;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvTotalAmount, tvTotalItems, tvConfirmOrder, tvSaveForLaterTitle;
    Activity activity;
    @SuppressLint("StaticFieldLeak")
    static Session session;
    static JSONObject jsonObject;
    View root;
    RecyclerView cartRecycleView, saveForLaterRecyclerView;
    NestedScrollView scrollView;
    double total;
    Button btnShowNow;
    DatabaseHelper databaseHelper;
    TextView tvTitleLocation;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvLocation;
    private ShimmerFrameLayout mShimmerViewContainer;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout lytSaveForLater;
    ArrayList<String> variantIdList, qtyList;
    public static SwipeRefreshLayout.OnRefreshListener refreshListener;
    public static boolean isDeliverable = false;
    public static boolean isSoldOut = false;

    @SuppressLint("SetTextI18n")
    public static void setData(Activity activity) {
        tvTotalAmount.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat(String.valueOf(Constant.FLOAT_TOTAL_AMOUNT)));
        int count;
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            count = carts.size();
        } else {
            count = offlineCarts.size();
        }
        tvTotalItems.setText(count + (count > 1 ? activity.getString(R.string.items) : activity.getString(R.string.item)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        values = new HashMap<>();
        saveForLaterValues = new HashMap<>();
        activity = getActivity();
        session = new Session(getActivity());
        lytTotal = root.findViewById(R.id.lytTotal);
        lytEmpty = root.findViewById(R.id.lytEmpty);
        btnShowNow = root.findViewById(R.id.btnShowNow);
        tvTotalAmount = root.findViewById(R.id.tvTotalAmount);
        tvTotalItems = root.findViewById(R.id.tvTotalItems);
        lytSaveForLater = root.findViewById(R.id.lytSaveForLater);
        tvSaveForLaterTitle = root.findViewById(R.id.tvSaveForLaterTitle);
        scrollView = root.findViewById(R.id.scrollView);
        cartRecycleView = root.findViewById(R.id.cartRecycleView);
        saveForLaterRecyclerView = root.findViewById(R.id.saveForLaterRecyclerView);
        tvConfirmOrder = root.findViewById(R.id.tvConfirmOrder);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        tvLocation = root.findViewById(R.id.tvLocation);
        tvTitleLocation = root.findViewById(R.id.tvTitleLocation);

        databaseHelper = new DatabaseHelper(activity);

        setHasOptionsMenu(true);

        tvLocation.setText(session.getData(Constant.GET_SELECTED_PINCODE_NAME));

        variantIdList = new ArrayList<>();
        qtyList = new ArrayList<>();

        cartRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        saveForLaterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        GetSettings(activity);

        refreshListener = () -> GetSettings(activity);

        tvTitleLocation.setOnClickListener(v -> {
            session.setBoolean(Constant.GET_SELECTED_PINCODE, true);
            session.setData(Constant.GET_SELECTED_PINCODE_ID, Constant.DEF_PINCODEID);
            session.setData(Constant.GET_SELECTED_PINCODE_NAME, Constant.DEF_PINCODENAME);
        });

        tvLocation.setOnClickListener(v -> {

            session.setBoolean(Constant.GET_SELECTED_PINCODE, true);
            session.setData(Constant.GET_SELECTED_PINCODE_ID, Constant.DEF_PINCODEID);
            session.setData(Constant.GET_SELECTED_PINCODE_NAME, Constant.DEF_PINCODENAME);
        });

        tvConfirmOrder.setOnClickListener(v -> {
            if (!isSoldOut && !isDeliverable) {
                if (Float.parseFloat(session.getData(Constant.min_order_amount)) <= Constant.FLOAT_TOTAL_AMOUNT) {
                    if (session.getBoolean(Constant.IS_USER_LOGIN)) {
                        if (values.size() > 0) {
                            ApiConfig.AddMultipleProductInCart(session, activity, values);
                        }
                        Constant.selectedAddressId = "";
                        Fragment fragment = new AddressListFragment();
                        final Bundle bundle = new Bundle();
                        bundle.putString(Constant.FROM, "process");
                        bundle.putDouble("total", Constant.FLOAT_TOTAL_AMOUNT);
                        bundle.putStringArrayList("variantIdList", variantIdList);
                        bundle.putStringArrayList("qtyList", qtyList);
                        fragment.setArguments(bundle);
                        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
                    } else {
                        startActivity(new Intent(activity, LoginActivity.class).putExtra("total", Constant.FLOAT_TOTAL_AMOUNT).putExtra(Constant.FROM, "checkout"));
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.msg_minimum_order_amount) + session.getData(Constant.CURRENCY) + ApiConfig.StringFormat(session.getData(Constant.min_order_amount)), Toast.LENGTH_SHORT).show();
                }
            } else if (isDeliverable) {
                Toast.makeText(activity, getString(R.string.msg_non_deliverable), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, getString(R.string.msg_sold_out), Toast.LENGTH_SHORT).show();
            }

        });

        btnShowNow.setOnClickListener(v -> MainActivity.fm.popBackStack());

        return root;
    }

    private void GetOfflineCart() {
        CartFragment.isDeliverable = false;
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_VARIANTS_OFFLINE, Constant.GetVal);
        params.put(Constant.VARIANT_IDs, databaseHelper.getCartList().toString().replace("[", "").replace("]", "").replace("\"", ""));
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("CARTLOAD",String.valueOf(jsonObject));
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        session.setData(Constant.TOTAL, jsonObject.getString(Constant.TOTAL));

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                        Gson g = new Gson();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            OfflineCart cart = g.fromJson(jsonObject1.toString(), OfflineCart.class);

                            variantIdList.add(cart.getProduct_variant_id());
                            qtyList.add(databaseHelper.CheckCartItemExist(cart.getProduct_variant_id(), cart.getProduct_id()));

                            double price;
                            String taxPercentage = "0";

                            try {
                                taxPercentage = (Double.parseDouble(cart.getItem().get(0).getTax_percentage()) > 0 ? cart.getItem().get(0).getTax_percentage() : "0");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (cart.getItem().get(0).getDiscounted_price().equals("0") || cart.getItem().get(0).getDiscounted_price().equals("")) {
                                price = ((Float.parseFloat(cart.getItem().get(0).getPrice()) + ((Float.parseFloat(cart.getItem().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                            } else {
                                price = ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                            }

                            Constant.FLOAT_TOTAL_AMOUNT += (price * Integer.parseInt(databaseHelper.CheckCartItemExist(cart.getProduct_variant_id(), cart.getProduct_id())));

                            offlineCarts.add(cart);
                        }

                        offlineCartAdapter = new OfflineCartAdapter(activity);
                        cartRecycleView.setAdapter(offlineCartAdapter);

                        setData(activity);

                        lytTotal.setVisibility(View.VISIBLE);

                    }
                    GetOfflineSaveForLater();
                } catch (JSONException e) {
                    GetOfflineSaveForLater();

                }
            } else {
                GetOfflineSaveForLater();
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }


    private void GetOfflineSaveForLater() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_VARIANTS_OFFLINE, Constant.GetVal);
        params.put(Constant.VARIANT_IDs, databaseHelper.getSaveForLaterList().toString().replace("[", "").replace("]", "").replace("\"", ""));
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            OfflineCart cart = new Gson().fromJson(jsonObject1.toString(), OfflineCart.class);
                            offlineSaveForLaterItems.add(cart);
                        }

                        offlineSaveForLaterAdapter = new OfflineSaveForLaterAdapter(activity);
                        saveForLaterRecyclerView.setAdapter(offlineSaveForLaterAdapter);

                        tvSaveForLaterTitle.setText(activity.getResources().getString(R.string.save_for_later) + " (" + offlineSaveForLaterItems.size() + ")");

                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        lytSaveForLater.setVisibility(View.VISIBLE);
                    } else {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        lytSaveForLater.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    lytSaveForLater.setVisibility(View.GONE);

                }
            } else {
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                lytSaveForLater.setVisibility(View.GONE);
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }

    public void GetSettings(final Activity activity) {
        Constant.FLOAT_TOTAL_AMOUNT = 0.00;
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Session session = new Session(activity);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_TIMEZONE, Constant.GetVal);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONObject object = jsonObject.getJSONObject(Constant.SETTINGS);


                        session.setData(Constant.minimum_version_required, object.getString(Constant.minimum_version_required));
                        session.setData(Constant.is_version_system_on, object.getString(Constant.is_version_system_on));

                        session.setData(Constant.CURRENCY, object.getString(Constant.CURRENCY));

                        session.setData(Constant.min_order_amount, object.getString(Constant.min_order_amount));
                        session.setData(Constant.max_cart_items_count, object.getString(Constant.max_cart_items_count));
                        session.setData(Constant.area_wise_delivery_charge, object.getString(Constant.area_wise_delivery_charge));

                        if (session.getBoolean(Constant.IS_USER_LOGIN)) {

                            getCartData();
                        } else {

                            offlineCarts = new ArrayList<>();
                            offlineCartAdapter = new OfflineCartAdapter(activity);
                            cartRecycleView.setAdapter(offlineCartAdapter);

                            offlineSaveForLaterItems = new ArrayList<>();
                            offlineSaveForLaterAdapter = new OfflineSaveForLaterAdapter(activity);
                            saveForLaterRecyclerView.setAdapter(offlineSaveForLaterAdapter);


                            GetOfflineCart();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    private void getCartData() {
        CartFragment.isDeliverable = false;

        carts = new ArrayList<>();
        cartAdapter = new CartAdapter(activity);
        cartRecycleView.setAdapter(cartAdapter);

        saveForLater = new ArrayList<>();
        saveForLaterAdapter = new SaveForLaterAdapter(activity);
        saveForLaterRecyclerView.setAdapter(saveForLaterAdapter);

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_CART, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1 != null) {
                                Cart cart = new Gson().fromJson(jsonObject1.toString(), Cart.class);

                                variantIdList.add(cart.getProduct_variant_id());
                                qtyList.add(cart.getQty());

                                double price;
                                String taxPercentage = "0";

                                try {
                                    taxPercentage = (Double.parseDouble(cart.getItems().get(0).getTax_percentage()) > 0 ? cart.getItems().get(0).getTax_percentage() : "0");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                                    price = ((Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                                } else {
                                    price = ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                                }

                                Constant.FLOAT_TOTAL_AMOUNT += (price * Double.parseDouble(cart.getQty()));
                                carts.add(cart);
                            } else {
                                break;
                            }
                        }

                        setData(activity);


                        JSONArray jsonArraySaveForLater = object.getJSONArray(Constant.SAVE_FOR_LATER);

                        for (int i = 0; i < jsonArraySaveForLater.length(); i++) {
                            JSONObject jsonObject1 = jsonArraySaveForLater.getJSONObject(i);
                            if (jsonObject1 != null) {
                                Cart cart = new Gson().fromJson(jsonObject1.toString(), Cart.class);
                                saveForLater.add(cart);
                            } else {
                                break;
                            }
                        }

                        cartAdapter = new CartAdapter(activity);
                        cartRecycleView.setAdapter(cartAdapter);

                        tvSaveForLaterTitle.setText(activity.getResources().getString(R.string.save_for_later) + " (" + saveForLater.size() + ")");

                        if (jsonArraySaveForLater.length() == 0) {
                            lytSaveForLater.setVisibility(View.GONE);
                        } else {
                            lytSaveForLater.setVisibility(View.VISIBLE);
                            saveForLaterAdapter = new SaveForLaterAdapter(activity);
                            saveForLaterRecyclerView.setAdapter(saveForLaterAdapter);
                        }

                        lytTotal.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        total = Double.parseDouble(jsonObject.getString(Constant.TOTAL));
                        session.setData(Constant.TOTAL, String.valueOf(total));
                        Constant.TOTAL_CART_ITEM = Integer.parseInt(jsonObject.getString(Constant.TOTAL));

                    } else {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        lytEmpty.setVisibility(View.VISIBLE);
                        lytTotal.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);

                }
            }
        }, activity, Constant.CART_URL, params, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            if (values.size() > 0) {
                ApiConfig.AddMultipleProductInCart(session, activity, values);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.cart);
        activity.invalidateOptionsMenu();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_cart).setVisible(false);
        menu.findItem(R.id.toolbar_layout).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
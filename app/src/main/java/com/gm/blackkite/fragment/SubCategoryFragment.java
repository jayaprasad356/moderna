package com.gm.blackkite.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.gm.blackkite.adapter.ProductLoadMoreAdapter;
import com.gm.blackkite.adapter.SubCategoryAdapter;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.Category;
import com.gm.blackkite.model.Product;


@SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
public class SubCategoryFragment extends Fragment {
    public static ArrayList<Product> productArrayList;
    public static ArrayList<Category> categoryArrayList;
    public ProductLoadMoreAdapter productLoadMoreAdapter;
    View root;
    Session session;
    int total;
    NestedScrollView nestedScrollView;
    Activity activity;
    int offset = 0;
    String id, filterBy, from;
    RecyclerView recyclerView, subCategoryRecycleView;
    SwipeRefreshLayout swipeLayout;
    int filterIndex;
    boolean isLoadMore = false,isGrid = false;
    int resource;
    private ShimmerFrameLayout mShimmerViewContainer;
    TextView tvAlert;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_sub_category, container, false);
        getAllWidgets(root);
        setHasOptionsMenu(true);
        offset = 0;
        activity = getActivity();

        session = new Session(activity);

        assert getArguments() != null;
        from = getArguments().getString(Constant.FROM);
        id = getArguments().getString("id");

        if (session.getBoolean("grid")) {
            resource = R.layout.lyt_item_grid;
            isGrid = true;
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        } else {
            resource = R.layout.lyt_item_list;
            isGrid = false;
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }

        filterIndex = -1;

            ApiConfig.GetSettings(activity);
            GetCategory();
            GetProducts();


        swipeLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(false);
            GetCategory();
            GetProducts();
            Log.d("OFFSET_COUNT_SWIPE",""+offset);
        });

        return root;
    }


    public void getAllWidgets(View root) {
        tvAlert = root.findViewById(R.id.tvNoData);
        recyclerView = root.findViewById(R.id.recyclerView);
        swipeLayout = root.findViewById(R.id.swipeLayout);
        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        subCategoryRecycleView = root.findViewById(R.id.subCategoryRecycleView);
        subCategoryRecycleView.setLayoutManager(new GridLayoutManager(activity, Constant.GRID_COLUMN));

    }

    public void stopShimmer() {
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }

    public void startShimmer() {
        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
    }

    void GetCategory() {
        startShimmer();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, id);

        categoryArrayList = new ArrayList<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(Constant.ERROR)) {
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setId(jsonObject.getString(Constant.ID));
                            category.setCategory_id(jsonObject.getString(Constant.CATEGORY_ID));
                            category.setName(jsonObject.getString(Constant.NAME));
                            category.setSlug(jsonObject.getString(Constant.SLUG));
                            category.setSubtitle(jsonObject.getString(Constant.SUBTITLE));
                            category.setImage(jsonObject.getString(Constant.IMAGE));
                            categoryArrayList.add(category);
                        }
                        subCategoryRecycleView.setAdapter(new SubCategoryAdapter(activity, categoryArrayList, R.layout.lyt_subcategory, "sub_cate"));
                    }
                    stopShimmer();
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopShimmer();
                }
            }
        }, activity, Constant.GET_SUB_CATEGORY_URL, params, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    void GetProducts() {

        startShimmer();
        productArrayList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, id);
        params.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }
        params.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, "" + offset);
        if (filterIndex != -1) {
            params.put(Constant.SORT, filterBy);
        }
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if (!jsonObject1.getBoolean(Constant.ERROR)) {
                        total = Integer.parseInt(jsonObject1.getString(Constant.TOTAL));
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Product product = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Product.class);
                                productArrayList.add(product);
                            }
                        } catch (Exception e) {
                            stopShimmer();
                        }
                        if (offset == 0) {
                            productLoadMoreAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, from);
                            recyclerView.setAdapter(productLoadMoreAdapter);
                            nestedScrollView.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mShimmerViewContainer.stopShimmer();
                            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                // if (diff == 0) {
                                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                    if (productArrayList.size() < total) {
                                        if (!isLoadMore) {
                                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                //bottom of list!
                                                productArrayList.add(null);
                                                productLoadMoreAdapter.notifyItemInserted(productArrayList.size() - 1);


                                                offset += Integer.parseInt("" + Constant.LOAD_ITEM_LIMIT);
                                                Map<String, String> params1 = new HashMap<>();
                                                params1.put(Constant.CATEGORY_ID, id);
                                                params1.put(Constant.USER_ID, session.getData(Constant.ID));
                                                params1.put(Constant.LIMIT, "" + Constant.LOAD_ITEM_LIMIT);
                                                params1.put(Constant.GET_ALL_PRODUCTS, Constant.GetVal);
                                                params1.put(Constant.OFFSET, offset + "");
                                                if (filterIndex != -1) {
                                                    params1.put(Constant.SORT, filterBy);
                                                }
                                                ApiConfig.RequestToVolley((result1, response1) -> {

                                                    if (result1) {
                                                        try {


                                                            productArrayList.remove(productArrayList.size() - 1);
                                                            productLoadMoreAdapter.notifyItemRemoved(productArrayList.size());
                                                            JSONObject jsonObject11 = new JSONObject(response1);

                                                            if (!jsonObject11.getBoolean(Constant.ERROR)) {
                                                                JSONObject object1 = new JSONObject(response1);
                                                                JSONArray jsonArray1 = object1.getJSONArray(Constant.DATA);


                                                                try {
                                                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                                                        Product product = new Gson().fromJson(jsonArray1.getJSONObject(i).toString(), Product.class);
                                                                        productArrayList.add(product);
                                                                    }
                                                                } catch (Exception e) {
                                                                    nestedScrollView.setVisibility(View.VISIBLE);
                                                                    mShimmerViewContainer.setVisibility(View.GONE);
                                                                    mShimmerViewContainer.stopShimmer();
                                                                }

                                                                productLoadMoreAdapter.notifyDataSetChanged();
                                                                isLoadMore = false;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();

                                                        }
                                                    }
                                                }, activity, Constant.GET_PRODUCTS_URL, params1, false);
                                                isLoadMore = true;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        stopShimmer();
                    }
                } catch (JSONException e) {
                    stopShimmer();
                }
            }
        }, activity, Constant.GET_PRODUCTS_URL, params, false);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_layout) {
            if (isGrid) {
                isGrid = false;
                recyclerView.setAdapter(null);
                resource = R.layout.lyt_item_list;
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            } else {
                isGrid = true;
                recyclerView.setAdapter(null);
                resource = R.layout.lyt_item_grid;
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }
            session.setBoolean("grid", isGrid);
            productLoadMoreAdapter = new ProductLoadMoreAdapter(activity, productArrayList, resource, from);
            recyclerView.setAdapter(productLoadMoreAdapter);
            productLoadMoreAdapter.notifyDataSetChanged();
            activity.invalidateOptionsMenu();
        }

        return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setIcon(ApiConfig.buildCounterDrawable(Constant.TOTAL_CART_ITEM, activity));

        menu.findItem(R.id.toolbar_layout).setVisible(true);

        Drawable myDrawable;
        if (isGrid) {

            myDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_list_);   // The ID of your drawable
        } else {
            myDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_grid_);    // The ID of your drawable.
        }
        menu.findItem(R.id.toolbar_layout).setIcon(myDrawable);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        assert getArguments() != null;
        Constant.TOOLBAR_TITLE = getArguments().getString(Constant.NAME);
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
    public void onPause() {
        super.onPause();
        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
    }
}
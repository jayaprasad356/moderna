package com.gm.blackkite.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.gm.blackkite.R;
import com.gm.blackkite.activity.MainActivity;
import com.gm.blackkite.adapter.CategoryAdapter;
import com.gm.blackkite.adapter.OfferAdapter;
import com.gm.blackkite.adapter.SectionAdapter;
import com.gm.blackkite.adapter.SellerAdapter;
import com.gm.blackkite.adapter.SliderAdapter;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.Category;
import com.gm.blackkite.model.Seller;
import com.gm.blackkite.model.Slider;

public class HomeFragment extends Fragment {

    public Session session;
    public static ArrayList<Category> categoryArrayList, sectionList;
    public static ArrayList<Seller> sellerArrayList;
    ArrayList<Slider> sliderArrayList;
    Activity activity;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeLayout;
    View root;
    int timerDelay = 0, timerWaiting = 0;
    EditText searchView;
    RecyclerView categoryRecyclerView, sectionView, offerView, sellerRecyclerView;
    ViewPager mPager;
    LinearLayout mMarkersLayout;
    int size;
    Timer swipeTimer;
    Handler handler;
    Runnable Update;
    int currentPage = 0;
    RelativeLayout lytCategory, lytSeller;
    LinearLayout lytSearchView;
    Menu menu;
    TextView tvMore, tvMoreSeller;
    boolean searchVisible = false;
    private ShimmerFrameLayout mShimmerViewContainer;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvLocation;
    public TextView tvTitleLocation;
    public static SwipeRefreshLayout.OnRefreshListener refreshListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        session = new Session(activity);

        timerDelay = 3000;
        timerWaiting = 3000;
        setHasOptionsMenu(true);

        swipeLayout = root.findViewById(R.id.swipeLayout);
        categoryRecyclerView = root.findViewById(R.id.categoryRecycleView);

        sectionView = root.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(activity));
        sectionView.setNestedScrollingEnabled(false);

        offerView = root.findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(activity));
        offerView.setNestedScrollingEnabled(false);

        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        lytCategory = root.findViewById(R.id.lytCategory);
        lytSeller = root.findViewById(R.id.lytSeller);
        lytSearchView = root.findViewById(R.id.lytSearchView);
        sellerRecyclerView = root.findViewById(R.id.sellerRecyclerView);
        tvMore = root.findViewById(R.id.tvMore);
        tvMoreSeller = root.findViewById(R.id.tvMoreSeller);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        tvTitleLocation = root.findViewById(R.id.tvTitleLocation);
        tvLocation = root.findViewById(R.id.tvLocation);

        searchView = root.findViewById(R.id.searchView);

        if (!session.getBoolean(Constant.GET_SELECTED_PINCODE)) {
            session.setBoolean(Constant.GET_SELECTED_PINCODE, true);
            session.setData(Constant.GET_SELECTED_PINCODE_ID, Constant.DEF_PINCODEID);
            session.setData(Constant.GET_SELECTED_PINCODE_NAME, Constant.DEF_PINCODENAME);
        } else {
            tvLocation.setText(session.getData(Constant.GET_SELECTED_PINCODE_NAME));
        }

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

        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                Rect scrollBounds = new Rect();
                nestedScrollView.getHitRect(scrollBounds);
                if (!lytSearchView.getLocalVisibleRect(scrollBounds) || scrollBounds.height() < lytSearchView.getHeight()) {
                    searchVisible = true;
                    menu.findItem(R.id.toolbar_search).setVisible(true);
                } else {
                    searchVisible = false;
                    menu.findItem(R.id.toolbar_search).setVisible(false);
                }
                activity.invalidateOptionsMenu();
            });
        }

        tvMore.setOnClickListener(v -> {
            if (!MainActivity.categoryClicked) {
                MainActivity.fm.beginTransaction().add(R.id.container, MainActivity.categoryFragment).show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
                MainActivity.categoryClicked = true;
            } else {
                MainActivity.fm.beginTransaction().show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
            }
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.navCategory);
            MainActivity.active = MainActivity.categoryFragment;
        });

        tvMoreSeller.setOnClickListener(v -> MainActivity.fm.beginTransaction().add(R.id.container, new SellerListFragment()).addToBackStack(null).commit());

        searchView.setOnTouchListener((View v, MotionEvent event) -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "search");
            bundle.putString(Constant.NAME, activity.getString(R.string.search));
            bundle.putString(Constant.ID, "");
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            return false;
        });

        lytSearchView.setOnClickListener(v -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "search");
            bundle.putString(Constant.NAME, activity.getString(R.string.search));
            bundle.putString(Constant.ID, "");
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });

        mPager = root.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, activity);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        refreshListener = () -> {
            if (swipeTimer != null) {
                swipeTimer.cancel();
            }
            timerDelay = 3000;
            timerWaiting = 3000;
            ApiConfig.getWalletBalance(activity, session);
            if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
            }
            GetHomeData();

        };

        swipeLayout.setOnRefreshListener(() -> {
            swipeLayout.setRefreshing(false);
            refreshListener.onRefresh();
        });

        GetHomeData();
        if (new Session(activity).getBoolean(Constant.IS_USER_LOGIN)) {
            ApiConfig.getWalletBalance(activity, new Session(activity));
        }

        return root;
    }

    public void GetHomeData() {

        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        if (swipeTimer != null) {
            swipeTimer.cancel();
        }
        Map<String, String> params = new HashMap<>();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }
        if (session.getBoolean(Constant.GET_SELECTED_PINCODE) && !session.getData(Constant.GET_SELECTED_PINCODE_ID).equals("0")) {
            params.put(Constant.PINCODE_ID, session.getData(Constant.GET_SELECTED_PINCODE_ID));
        }

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        GetOfferImage(jsonObject.getJSONArray(Constant.OFFER_IMAGES));

                        GetCategory(jsonObject);
                        SectionProductRequest(jsonObject.getJSONArray(Constant.SECTIONS));
                        GetSlider(jsonObject.getJSONArray(Constant.SLIDER_IMAGES));
                        GetSeller(jsonObject.getJSONArray(Constant.SELLER));
                    } else {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    nestedScrollView.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmer();

                }
            }
        }, activity, Constant.GET_ALL_DATA_URL, params, false);
    }

    public void GetOfferImage(JSONArray jsonArray) {
        ArrayList<String> offerList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    offerList.add(object.getString(Constant.IMAGE));
                }
                offerView.setAdapter(new OfferAdapter(offerList, R.layout.offer_lyt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void GetCategory(JSONObject object) {
        categoryArrayList = new ArrayList<>();

        try {
            int visible_count;
            int column_count;

            JSONArray jsonArray = object.getJSONArray(Constant.CATEGORIES);

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category category = new Gson().fromJson(jsonObject.toString(), Category.class);
                    categoryArrayList.add(category);
                }

                if (!object.getString("style").equals("")) {
                    if (object.getString("style").equals("style_1")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        column_count = Integer.parseInt(object.getString("column_count"));
                        categoryRecyclerView.setLayoutManager(new GridLayoutManager(activity, column_count));
                        categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_grid, "home", visible_count));
                    } else if (object.getString("style").equals("style_2")) {
                        visible_count = Integer.parseInt(object.getString("visible_count"));
                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                        categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_list, "home", visible_count));
                    }
                } else {
                    categoryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_list, "home", 6));
                }

            } else {
                lytCategory.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SectionProductRequest(JSONArray jsonArray) {  //json request for product search
        sectionList = new ArrayList<>();
        try {
            for (int j = 0; j < jsonArray.length(); j++) {
                Category section = new Category();
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                section.setName(jsonObject.getString(Constant.TITLE));
                section.setId(jsonObject.getString(Constant.ID));
                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                section.setProductList(ApiConfig.GetProductList(productArray));
                sectionList.add(section);
            }
            sectionView.setVisibility(View.VISIBLE);
            SectionAdapter sectionAdapter = new SectionAdapter(activity, sectionList);
            sectionView.setAdapter(sectionAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void GetSlider(JSONArray jsonArray) {
        sliderArrayList = new ArrayList<>();
        try {
            size = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
            }
            mPager.setAdapter(new SliderAdapter(sliderArrayList, activity, R.layout.lyt_slider, "home"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, activity);
            handler = new Handler();
            Update = () -> {
                if (currentPage == size) {
                    currentPage = 0;
                }
                try {
                    mPager.setCurrentItem(currentPage++, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, timerDelay, timerWaiting);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }

    void GetSeller(JSONArray jsonArray) {
        try {
            sellerArrayList = new ArrayList<>();
            if (jsonArray.length() > 0) {
                //lytSeller.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Seller seller = new Gson().fromJson(jsonObject.toString(), Seller.class);
                    sellerArrayList.add(seller);
                }

                sellerRecyclerView.setLayoutManager(new GridLayoutManager(activity, Constant.GRID_COLUMN));
                sellerRecyclerView.setAdapter(new SellerAdapter(activity, sellerArrayList, R.layout.lyt_seller, "home", 6));
            } else {
                lytSeller.setVisibility(View.GONE);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.invalidateOptionsMenu();
        ApiConfig.GetSettings(activity);
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(searchVisible);
    }

}
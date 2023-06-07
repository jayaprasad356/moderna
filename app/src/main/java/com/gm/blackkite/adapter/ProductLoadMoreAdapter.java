package com.gm.blackkite.adapter;

import static com.gm.blackkite.helper.ApiConfig.AddOrRemoveFavorite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import com.gm.blackkite.R;
import com.gm.blackkite.fragment.FavoriteFragment;
import com.gm.blackkite.fragment.ProductDetailFragment;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.DatabaseHelper;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.PriceVariation;
import com.gm.blackkite.model.Product;

@SuppressLint("NotifyDataSetChanged")
public class ProductLoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    public int resource;
    public ArrayList<Product> mDataset;
    Activity activity;
    Session session;
    boolean isLogin;
    DatabaseHelper databaseHelper;
    String from;
    public boolean isLoading;
    boolean isFavorite;

    public ProductLoadMoreAdapter(Activity activity, ArrayList<Product> myDataset, int resource, String from) {
        this.activity = activity;
        this.mDataset = myDataset;
        this.resource = resource;
        this.from = from;
        this.session = new Session(activity);
        isLogin = session.getBoolean(Constant.IS_USER_LOGIN);
        Constant.CartValues = new HashMap<>();
        databaseHelper = new DatabaseHelper(activity);
    }

    public void add(int position, Product item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case (VIEW_TYPE_ITEM):
                view = LayoutInflater.from(activity).inflate(resource, parent, false);
                return new HolderItems(view);
            case (VIEW_TYPE_LOADING):
                view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
                return new ViewHolderLoading(view);
            default:
                throw new IllegalArgumentException("unexpected viewType: " + viewType);
        }
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, @SuppressLint("RecyclerView") int position) {

        if (holderParent instanceof HolderItems) {
            HolderItems holder = (HolderItems) holderParent;
            holder.setIsRecyclable(false);
            Product product = mDataset.get(position);

            ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
            if (priceVariations.size() == 1) {
                holder.spinner.setVisibility(View.INVISIBLE);
                holder.lytSpinner.setVisibility(View.INVISIBLE);
            }
//            if (!product.getIndicator().equals("0")) {
//                holder.imgIndicator.setVisibility(View.VISIBLE);
//                if (product.getIndicator().equals("1"))
//                    holder.imgIndicator.setImageResource(R.drawable.ic_veg_icon);
//                else if (product.getIndicator().equals("2"))
//                    holder.imgIndicator.setImageResource(R.drawable.ic_non_veg_icon);
//            }
            holder.productName.setText(Html.fromHtml(product.getName(), 0));

            Picasso.get().
                    load(product.getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgThumb);

            CustomAdapter customAdapter = new CustomAdapter(activity, priceVariations, holder, product);
            holder.spinner.setAdapter(customAdapter);

            holder.lytMain.setOnClickListener(v -> {

                if (Constant.CartValues.size() > 0) {
                    ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                }

                AppCompatActivity activity1 = (AppCompatActivity) activity;
                Fragment fragment = new ProductDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.VARIANT_POSITION, priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition());
                bundle.putString(Constant.ID, priceVariations.get(0).getProduct_id());
                bundle.putString(Constant.FROM, from);
                bundle.putInt(Constant.LIST_POSITION, position);

                fragment.setArguments(bundle);

                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

            });

            SetSelectedData(holder, priceVariations.get(0), product);


        } else if (holderParent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderParent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        Product product = mDataset.get(position);
        if (product != null)
            return Integer.parseInt(product.getPriceVariations().get(0).getProduct_id());
        else
            return position;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @SuppressLint("SetTextI18n")
    public void SetSelectedData(HolderItems holder, PriceVariation extra, Product product) {

//        GST_Amount (Original Cost x GST %)/100
//        Net_Price Original Cost + GST Amount

        holder.tvMeasurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());

        if (session.getBoolean(Constant.IS_USER_LOGIN)) {

            if (Constant.CartValues.containsKey(extra.getId())) {
                holder.tvQuantity.setText("" + Constant.CartValues.get(extra.getId()));
            }
        } else {
            if (session.getData(extra.getId()) != null) {
                holder.tvQuantity.setText(session.getData(extra.getId()));
            } else {
                holder.tvQuantity.setText(extra.getCart_count());
            }
        }

        if (isLogin) {
            holder.tvQuantity.setText(extra.getCart_count());

            if (product.isIs_favorite()) {
                holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
            } else {
                holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
            }
            Session session = new Session(activity);

            holder.imgFav.setOnClickListener(v -> {
                try {
                    isFavorite = product.isIs_favorite();
                    if (from.equals("favorite")) {
                        isFavorite = false;
                        mDataset.remove(product);
                        notifyDataSetChanged();
                        if(mDataset.size()==0){
                            FavoriteFragment.tvAlert.setVisibility(View.VISIBLE);
                        }else{
                            FavoriteFragment.tvAlert.setVisibility(View.GONE);
                        }
                    } else {
                        if (isFavorite) {
                            isFavorite = false;
                            holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                            holder.lottieAnimationView.setVisibility(View.GONE);
                        } else {
                            isFavorite = true;
                            holder.lottieAnimationView.setVisibility(View.VISIBLE);
                            holder.lottieAnimationView.playAnimation();
                        }
                        product.setIs_favorite(isFavorite);
                    }

                    AddOrRemoveFavorite(activity, session, extra.getProduct_id(), isFavorite);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        } else {

            holder.tvQuantity.setText(databaseHelper.CheckCartItemExist(product.getPriceVariations().get(0).getId(), product.getPriceVariations().get(0).getProduct_id()));

            if (databaseHelper.getFavoriteById(product.getPriceVariations().get(0).getProduct_id())) {
                holder.imgFav.setImageResource(R.drawable.ic_is_favorite);
            } else {
                holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
            }

            holder.imgFav.setOnClickListener(v -> {
                isFavorite = databaseHelper.getFavoriteById(product.getPriceVariations().get(0).getProduct_id());
                if (from.equals("favorite")) {
                    isFavorite = false;
                    mDataset.remove(product);
                    notifyDataSetChanged();

                    if (mDataset.size() == 0) {
                        FavoriteFragment.tvAlert.setVisibility(View.VISIBLE);
                    } else {
                        FavoriteFragment.tvAlert.setVisibility(View.GONE);
                    }
                } else {
                    if (isFavorite) {
                        isFavorite = false;
                        holder.imgFav.setImageResource(R.drawable.ic_is_not_favorite);
                        holder.lottieAnimationView.setVisibility(View.GONE);
                    } else {
                        isFavorite = true;
                        holder.lottieAnimationView.setVisibility(View.VISIBLE);
                        holder.lottieAnimationView.playAnimation();
                    }
                }
                databaseHelper.AddOrRemoveFavorite(extra.getProduct_id(), isFavorite);
            });
        }

        double DiscountedPrice, OriginalPrice;
        String taxPercentage = "0";
        try {
            taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.showDiscount.setVisibility(View.GONE);
            DiscountedPrice = ((Float.parseFloat(extra.getPrice()) + ((Float.parseFloat(extra.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            DiscountedPrice = ((Float.parseFloat(extra.getDiscounted_price()) + ((Float.parseFloat(extra.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            OriginalPrice = (Float.parseFloat(extra.getPrice()) + ((Float.parseFloat(extra.getPrice()) * Float.parseFloat(taxPercentage)) / 100));

            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.originalPrice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + OriginalPrice));

            holder.showDiscount.setVisibility(View.VISIBLE);
            holder.showDiscount.setText("-" + ApiConfig.GetDiscount(OriginalPrice, DiscountedPrice));
        }

        holder.productPrice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

        if (extra.getServe_for().equalsIgnoreCase(Constant.SOLD_OUT_TEXT)) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }
        holder.qtyLyt.setVisibility(View.GONE);

        if (isLogin) {
            if (Constant.CartValues.containsKey(extra.getId())) {
                holder.tvQuantity.setText("" + Constant.CartValues.get(extra.getId()));
            } else {
                holder.tvQuantity.setText(extra.getCart_count());
            }

            if (extra.getCart_count().equals("0")) {
                holder.btnAddToCart.setVisibility(View.VISIBLE);
            } else {
                holder.btnAddToCart.setVisibility(View.GONE);
            }
        } else {

            if (databaseHelper.CheckCartItemExist(extra.getId(), extra.getProduct_id()).equals("0")) {
                holder.btnAddToCart.setVisibility(View.VISIBLE);
            } else {
                holder.btnAddToCart.setVisibility(View.GONE);
            }

            holder.tvQuantity.setText(databaseHelper.CheckCartItemExist(extra.getId(), extra.getProduct_id()));
        }

        String maxCartCont;

        if (product.getTotal_allowed_quantity() == null || product.getTotal_allowed_quantity().equals("") || product.getTotal_allowed_quantity().equals("0")) {
            maxCartCont = session.getData(Constant.max_cart_items_count);
        } else {
            maxCartCont = product.getTotal_allowed_quantity();
        }

        holder.imgAdd.setOnClickListener(v -> addQuantity(extra, holder, true, maxCartCont));
        holder.imgMinus.setOnClickListener(v -> addQuantity(extra, holder, false, maxCartCont));
        holder.btnAddToCart.setOnClickListener(v -> addQuantity(extra, holder, true, maxCartCont));

    }

    @SuppressLint("SetTextI18n")
    public void addQuantity(PriceVariation extra, HolderItems holder, boolean isAdd, String maxCartCont) {
        try {
            if (session.getData(Constant.STATUS).equals("1")) {
                int count = Integer.parseInt(holder.tvQuantity.getText().toString());

                if (isAdd) {
                    count++;
                    if (Float.parseFloat(extra.getStock()) >= count) {
                        if (Float.parseFloat(maxCartCont) >= count) {
                            holder.tvQuantity.setText("" + count);
                            if (isLogin) {
                                if (Constant.CartValues.containsKey(extra.getId())) {
                                    Constant.CartValues.replace(extra.getId(), "" + count);
                                } else {
                                    Constant.CartValues.put(extra.getId(), "" + count);
                                }
                                ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                            } else {
                                databaseHelper.AddToCart(extra.getId(), extra.getProduct_id(), "" + count);
                                databaseHelper.getTotalItemOfCart(activity);
                                activity.invalidateOptionsMenu();
                            }
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.limit_alert), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    count--;
                    holder.tvQuantity.setText("" + count);
                    if (isLogin) {
                        if (Constant.CartValues.containsKey(extra.getId())) {
                            Constant.CartValues.replace(extra.getId(), "" + count);
                        } else {
                            Constant.CartValues.put(extra.getId(), "" + count);
                        }
                        ApiConfig.AddMultipleProductInCart(session, activity, Constant.CartValues);
                    } else {
                        databaseHelper.AddToCart(extra.getId(), extra.getProduct_id(), "" + count);
                        databaseHelper.getTotalItemOfCart(activity);
                        activity.invalidateOptionsMenu();
                    }
                }
                if (count == 0) {
                    holder.btnAddToCart.setVisibility(View.VISIBLE);
                } else {
                    holder.btnAddToCart.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.user_block_msg), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public static class HolderItems extends RecyclerView.ViewHolder {
        ImageButton imgAdd;
        ImageButton imgMinus;
        TextView productName;
        TextView productPrice;
        TextView tvQuantity;
        TextView tvMeasurement;
        TextView showDiscount;
        TextView originalPrice;
        TextView tvStatus;
        ImageView imgThumb;
        ImageView imgFav;
        ImageView imgIndicator;
        RelativeLayout lytSpinner;
        CardView lytMain;
        AppCompatSpinner spinner;
        RelativeLayout qtyLyt;
        LottieAnimationView lottieAnimationView;
        Button btnAddToCart;

        public HolderItems(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.tvPrice);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvMeasurement = itemView.findViewById(R.id.tvMeasurement);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
            imgAdd = itemView.findViewById(R.id.btnAddQuantity);
            imgMinus = itemView.findViewById(R.id.btnMinusQuantity);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            qtyLyt = itemView.findViewById(R.id.qtyLyt);
            imgFav = itemView.findViewById(R.id.imgFav);
            lytMain = itemView.findViewById(R.id.lytMain);
            spinner = itemView.findViewById(R.id.spinner);
            lytSpinner = itemView.findViewById(R.id.lytSpinner);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);

        }

    }

    public class CustomAdapter extends BaseAdapter {
        Activity activity;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        HolderItems holder;
        Product product;

        public CustomAdapter(Activity activity, ArrayList<PriceVariation> extraList, HolderItems holder, Product product) {
            this.activity = activity;
            this.extraList = extraList;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(activity));
        }

        @Override
        public int getCount() {
            return extraList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.tvMeasurement);

            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLD_OUT_TEXT)) {
                measurement.setTextColor(ContextCompat.getColor(activity, R.color.red));
            } else {
                measurement.setTextColor(ContextCompat.getColor(activity, R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PriceVariation priceVariation = extraList.get(i);
                    SetSelectedData(holder, priceVariation, product);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

}

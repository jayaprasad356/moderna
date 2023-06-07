package com.gm.blackkite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import com.gm.blackkite.R;
import com.gm.blackkite.fragment.CartFragment;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.DatabaseHelper;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.OfflineCart;


@SuppressLint("NotifyDataSetChanged")
public class OfflineSaveForLaterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // for load more
    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;
    final Activity activity;
    static DatabaseHelper databaseHelper;
    final Session session;


    public OfflineSaveForLaterAdapter(Activity activity) {
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
        session = new Session(activity);
    }

    public void add(int position, OfflineCart item) {
        if (position > 0) {
            CartFragment.offlineSaveForLaterItems.add(position, item);
        } else {
            CartFragment.offlineSaveForLaterItems.add(item);
        }

        CartFragment.offlineSaveForLaterAdapter.notifyDataSetChanged();
        CartFragment.offlineCartAdapter.notifyDataSetChanged();
    }

    public void removeItem(int position) {
        OfflineCart cart = CartFragment.offlineSaveForLaterItems.get(position);
        databaseHelper.RemoveFromSaveForLater(cart.getProduct_id(), cart.getProduct_variant_id());
        CartFragment.offlineSaveForLaterItems.remove(cart);
        CartFragment.offlineSaveForLaterAdapter.notifyDataSetChanged();
        CartFragment.offlineCartAdapter.notifyDataSetChanged();
        if (getItemCount() == 0)
            CartFragment.lytSaveForLater.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public void moveItem(int position) {
        try {
            OfflineCart cart = CartFragment.offlineSaveForLaterItems.get(position);

            CartFragment.isSoldOut = false;
            CartFragment.isDeliverable = false;

            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(cart.getItem().get(0).getTax_percentage()) > 0 ? cart.getItem().get(0).getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            double price;
            if (cart.getItem().get(0).getDiscounted_price().equals("0") || cart.getItem().get(0).getDiscounted_price().equals("")) {
                price = ((Float.parseFloat(cart.getItem().get(0).getPrice()) + ((Float.parseFloat(cart.getItem().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                price = ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItem().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            }

            Constant.FLOAT_TOTAL_AMOUNT += (price * Integer.parseInt(databaseHelper.CheckSaveForLaterItemExist(cart.getProduct_variant_id(), cart.getProduct_id())));

            CartFragment.setData(activity);

            CartFragment.offlineSaveForLaterItems.remove(cart);

            CartFragment.offlineCartAdapter.add(0, cart);

            if (CartFragment.offlineSaveForLaterItems.size()==0)
                CartFragment.lytSaveForLater.setVisibility(View.GONE);

            CartFragment.tvSaveForLaterTitle.setText(activity.getResources().getString(R.string.save_for_later) + " (" + CartFragment.offlineSaveForLaterItems.size() + ")");

            CartFragment.saveForLaterValues.remove(cart.getProduct_variant_id());

            Constant.TOTAL_CART_ITEM = CartFragment.offlineCarts.size();

            databaseHelper.MoveToCartOrSaveForLater(cart.getProduct_variant_id(), cart.getProduct_id(), "save_for_later", activity);

            CartFragment.offlineCartAdapter.notifyDataSetChanged();

            CartFragment.offlineSaveForLaterAdapter.notifyDataSetChanged();

            CartFragment.setData(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, final int viewType) {
        View view;
        switch (viewType) {
            case (VIEW_TYPE_ITEM):
                view = LayoutInflater.from(activity).inflate(R.layout.lyt_save_for_later, parent, false);
                return new HolderItems(view);
            case (VIEW_TYPE_LOADING):
                view = LayoutInflater.from(activity).inflate(R.layout.item_progressbar, parent, false);
                return new ViewHolderLoading(view);
            default:
                throw new IllegalArgumentException("unexpected viewType: " + viewType);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holderParent, final int position) {

        if (holderParent instanceof HolderItems) {
            final HolderItems holder = (HolderItems) holderParent;
            final OfflineCart cart = CartFragment.offlineSaveForLaterItems.get(position);

            Picasso.get()
                    .load(cart.getItem().get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgProduct);

            holder.tvProductName.setText(cart.getItem().get(0).getName());

            holder.tvMeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());

            double price, oPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(cart.getItem().get(0).getTax_percentage()) > 0 ? cart.getItem().get(0).getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cart.getDiscounted_price().equals("0") || cart.getDiscounted_price().equals("")) {
                price = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                price = ((Float.parseFloat(cart.getDiscounted_price()) + ((Float.parseFloat(cart.getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
                oPrice = ((Float.parseFloat(cart.getPrice()) + ((Float.parseFloat(cart.getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
                holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvOriginalPrice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + oPrice));
            }

            if (!cart.getItem().get(0).getServe_for().equalsIgnoreCase("available")) {
                holder.tvStatus.setVisibility(View.VISIBLE);
            }

            holder.tvPrice.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + price));

            holder.tvDelete.setOnClickListener(v -> removeItem(position));

            holder.tvAction.setOnClickListener(v -> moveItem(position));


            holder.tvProductName.setText(cart.getItem().get(0).getName());
            holder.tvMeasurement.setText(cart.getItem().get(0).getMeasurement() + "\u0020" + cart.getItem().get(0).getUnit());

            if (CartFragment.offlineSaveForLaterItems.size() > 0) {
                CartFragment.lytSaveForLater.setVisibility(View.VISIBLE);
            } else {
                CartFragment.lytSaveForLater.setVisibility(View.GONE);
            }

        } else if (holderParent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderParent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return CartFragment.offlineSaveForLaterItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return CartFragment.offlineSaveForLaterItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        OfflineCart cart = CartFragment.offlineSaveForLaterItems.get(position);
        if (cart != null)
            return Integer.parseInt(cart.getProduct_variant_id());
        else
            return position;
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public final ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.itemProgressbar);
        }
    }

    public static class HolderItems extends RecyclerView.ViewHolder {
        final ImageView imgProduct;
        final TextView tvProductName;
        final TextView tvMeasurement;
        final TextView tvPrice;
        final TextView tvOriginalPrice;
        final TextView tvDelete;
        final TextView tvAction;
        final TextView tvStatus;
        final RelativeLayout lytMain;

        public HolderItems(@NonNull View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvMeasurement = itemView.findViewById(R.id.tvMeasurement);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
        }
    }
}
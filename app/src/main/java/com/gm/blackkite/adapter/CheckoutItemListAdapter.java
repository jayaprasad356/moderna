package com.gm.blackkite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.gm.blackkite.R;
import com.gm.blackkite.fragment.CheckoutFragment;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.Cart;

/**
 * Created by shree1 on 3/16/2017.
 */

public class CheckoutItemListAdapter extends RecyclerView.Adapter<CheckoutItemListAdapter.ItemHolder> {

    public ArrayList<Cart> carts;
    public Activity activity;
    Session session;

    public CheckoutItemListAdapter(Activity activity, ArrayList<Cart> carts) {
        try {
            this.activity = activity;
            this.carts = carts;
            session = new Session(activity);
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final ItemHolder holder, final int position) {
        try {

            final Cart cart = carts.get(position);

            float DiscountedPrice;
            if (cart.getItems().get(0).getDiscounted_price().equals("0")) {
                DiscountedPrice = Float.parseFloat(cart.getItems().get(0).getPrice());
            } else {
                DiscountedPrice = Float.parseFloat(cart.getItems().get(0).getDiscounted_price());
            }

            String taxPercentage = cart.getItems().get(0).getTax_percentage();

            holder.tvItemName.setText(cart.getItems().get(0).getName() + " (" + cart.getItems().get(0).getMeasurement() + " " + ApiConfig.toTitleCase(cart.getItems().get(0).getUnit()) + ")");

            holder.tvQty.setText(activity.getString(R.string.qty_1) + cart.getQty());

            holder.tvPrice.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

            if (!cart.getItems().get(0).getCod_allowed().equals("0"))
                Constant.isCODAllow = false;

            if (!Boolean.parseBoolean(cart.getItems().get(0).getIs_item_deliverable())) {
                CheckoutFragment.OrderPlacable = true;
                holder.tvDeliverable.setVisibility(View.VISIBLE);
                holder.tvDeliverable.setText(activity.getString(R.string.msg_item_not_deliverable));
            } else if (cart.getItems().get(0).getServe_for().equalsIgnoreCase(Constant.SOLD_OUT_TEXT)) {
                CheckoutFragment.OrderPlacable = true;
                holder.tvDeliverable.setVisibility(View.VISIBLE);
                holder.tvDeliverable.setText(activity.getString(R.string.sold_out));
            } else {
                holder.tvDeliverable.setVisibility(View.GONE);
            }

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100))));
            } else {
                holder.tvTaxTitle.setText(cart.getItems().get(0).getTax_title());
                holder.tvTaxAmount.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100))));
            }
            if (cart.getItems().get(0).getTax_percentage().equals("0")) {
                holder.tvTaxTitle.setText("TAX");
            }
            holder.tvTaxPercent.setText("(" + cart.getItems().get(0).getTax_percentage() + "%)");

            if (cart.getItems().get(0).getDiscounted_price().equals("0") || cart.getItems().get(0).getDiscounted_price().equals("")) {
                holder.tvSubTotal.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getPrice()) + ((Float.parseFloat(cart.getItems().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)))));
            } else {
                holder.tvSubTotal.setText(session.getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + (Integer.parseInt(cart.getQty()) * (Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) + ((Float.parseFloat(cart.getItems().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)))));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_checkout_item_list, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        public final TextView tvItemName;
        public final TextView tvQty;
        public final TextView tvPrice;
        public final TextView tvSubTotal;
        public final TextView tvTaxPercent;
        public final TextView tvTaxTitle;
        public final TextView tvTaxAmount;
        public final TextView tvDeliverable;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
            tvTaxPercent = itemView.findViewById(R.id.tvTaxPercent);
            tvTaxTitle = itemView.findViewById(R.id.tvTaxTitle);
            tvTaxAmount = itemView.findViewById(R.id.tvTaxAmount);
            tvDeliverable = itemView.findViewById(R.id.tvDeliverable);
        }


    }
}


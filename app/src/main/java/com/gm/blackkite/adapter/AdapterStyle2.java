package com.gm.blackkite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.gm.blackkite.R;
import com.gm.blackkite.fragment.ProductDetailFragment;
import com.gm.blackkite.helper.ApiConfig;
import com.gm.blackkite.helper.Constant;
import com.gm.blackkite.helper.Session;
import com.gm.blackkite.model.Product;


/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle2 extends RecyclerView.Adapter<AdapterStyle2.VideoHolder> {

    public final ArrayList<Product> productList;
    public final Activity activity;

    public AdapterStyle2(Activity activity, ArrayList<Product> productList) {
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, final int position) {

        if (productList.size() > 0) {

            double DiscountedPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(productList.get(0).getTax_percentage()) > 0 ? productList.get(0).getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (productList.get(0).getPriceVariations().get(0).getDiscounted_price().equals("0") || productList.get(0).getPriceVariations().get(0).getDiscounted_price().equals("")) {
                DiscountedPrice = ((Float.parseFloat(productList.get(0).getPriceVariations().get(0).getPrice()) + ((Float.parseFloat(productList.get(0).getPriceVariations().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                DiscountedPrice = ((Float.parseFloat(productList.get(0).getPriceVariations().get(0).getDiscounted_price()) + ((Float.parseFloat(productList.get(0).getPriceVariations().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            }
            holder.tvSubStyle2_1.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

            holder.tvStyle2_1.setText(productList.get(0).getName());

            Picasso.get()
                    .load(productList.get(0).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgStyle2_1);
        }
        if (productList.size() > 1) {
            holder.tvStyle2_2.setText(productList.get(1).getName());

            double DiscountedPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(productList.get(1).getTax_percentage()) > 0 ? productList.get(1).getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (productList.get(1).getPriceVariations().get(0).getDiscounted_price().equals("0") || productList.get(1).getPriceVariations().get(0).getDiscounted_price().equals("")) {
                DiscountedPrice = ((Float.parseFloat(productList.get(1).getPriceVariations().get(0).getPrice()) + ((Float.parseFloat(productList.get(1).getPriceVariations().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                DiscountedPrice = ((Float.parseFloat(productList.get(1).getPriceVariations().get(0).getDiscounted_price()) + ((Float.parseFloat(productList.get(1).getPriceVariations().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            }
            holder.tvSubStyle2_2.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

            Picasso.get()
                    .load(productList.get(1).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgStyle2_2);
        }

        if (productList.size() > 2) {
            holder.tvStyle2_3.setText(productList.get(2).getName());

            double DiscountedPrice;
            String taxPercentage = "0";
            try {
                taxPercentage = (Double.parseDouble(productList.get(2).getTax_percentage()) > 0 ? productList.get(2).getTax_percentage() : "0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (productList.get(2).getPriceVariations().get(0).getDiscounted_price().equals("0") || productList.get(2).getPriceVariations().get(0).getDiscounted_price().equals("")) {
                DiscountedPrice = ((Float.parseFloat(productList.get(2).getPriceVariations().get(0).getPrice()) + ((Float.parseFloat(productList.get(2).getPriceVariations().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
            } else {
                DiscountedPrice = ((Float.parseFloat(productList.get(2).getPriceVariations().get(0).getDiscounted_price()) + ((Float.parseFloat(productList.get(2).getPriceVariations().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
            }

            holder.tvSubStyle2_3.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

            Picasso.get()
                    .load(productList.get(2).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgStyle2_3);
        }

        holder.layoutStyle2_1.setOnClickListener(view -> {
            if (productList.size() >= 1) {
                AppCompatActivity activity1 = (AppCompatActivity) activity;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "section");
                bundle.putInt(Constant.VARIANT_POSITION, 0);
                bundle.putString(Constant.ID, productList.get(0).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        holder.layoutStyle2_2.setOnClickListener(view -> {
            if (productList.size() >= 2) {
                AppCompatActivity activity1 = (AppCompatActivity) activity;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "section");
                bundle.putInt(Constant.VARIANT_POSITION, 0);
                bundle.putString(Constant.ID, productList.get(1).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        holder.layoutStyle2_3.setOnClickListener(view -> {
            if (productList.size() >= 3) {
                AppCompatActivity activity1 = (AppCompatActivity) activity;
                Fragment fragment = new ProductDetailFragment();
                final Bundle bundle = new Bundle();
                bundle.putString(Constant.FROM, "section");
                bundle.putInt(Constant.VARIANT_POSITION, 0);
                bundle.putString(Constant.ID, productList.get(2).getId());
                fragment.setArguments(bundle);
                activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            }
        });
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lyt_style_2, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class VideoHolder extends RecyclerView.ViewHolder {

        public final ImageView imgStyle2_1;
        public final ImageView imgStyle2_2;
        public final ImageView imgStyle2_3;
        public final TextView tvStyle2_1;
        public final TextView tvStyle2_2;
        public final TextView tvStyle2_3;
        public final TextView tvSubStyle2_1;
        public final TextView tvSubStyle2_2;
        public final TextView tvSubStyle2_3;
        public final CardView layoutStyle2_1;
        public final CardView layoutStyle2_2;
        public final CardView layoutStyle2_3;

        public VideoHolder(View itemView) {
            super(itemView);
            imgStyle2_1 = itemView.findViewById(R.id.imgStyle2_1);
            imgStyle2_2 = itemView.findViewById(R.id.imgStyle2_2);
            imgStyle2_3 = itemView.findViewById(R.id.imgStyle2_3);
            tvStyle2_1 = itemView.findViewById(R.id.tvStyle2_1);
            tvStyle2_2 = itemView.findViewById(R.id.tvStyle2_2);
            tvStyle2_3 = itemView.findViewById(R.id.tvStyle2_3);
            tvSubStyle2_1 = itemView.findViewById(R.id.tvSubStyle2_1);
            tvSubStyle2_2 = itemView.findViewById(R.id.tvSubStyle2_2);
            tvSubStyle2_3 = itemView.findViewById(R.id.tvSubStyle2_3);
            layoutStyle2_1 = itemView.findViewById(R.id.layoutStyle2_1);
            layoutStyle2_2 = itemView.findViewById(R.id.layoutStyle2_2);
            layoutStyle2_3 = itemView.findViewById(R.id.layoutStyle2_3);
        }


    }
}
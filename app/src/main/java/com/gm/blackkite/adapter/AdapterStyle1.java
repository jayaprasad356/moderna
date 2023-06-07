package com.gm.blackkite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AdapterStyle1 extends RecyclerView.Adapter<AdapterStyle1.VideoHolder> {

    public final ArrayList<Product> productList;
    public final Activity activity;
    public final int itemResource;

    public AdapterStyle1(Activity activity, ArrayList<Product> productList, int itemResource) {
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;

    }

    @Override
    public int getItemCount() {
        return Math.min(productList.size(), 4);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final Product product = productList.get(position);


        Picasso.get()
                .load(product.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.thumbnail);


        holder.tvTitle.setText(product.getName());

        double DiscountedPrice;
        String taxPercentage = "0";
        try {
            taxPercentage = (Double.parseDouble(product.getTax_percentage()) > 0 ? product.getTax_percentage() : "0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (product.getPriceVariations().get(0).getDiscounted_price().equals("0") || product.getPriceVariations().get(0).getDiscounted_price().equals("")) {
            DiscountedPrice = ((Float.parseFloat(product.getPriceVariations().get(0).getPrice()) + ((Float.parseFloat(product.getPriceVariations().get(0).getPrice()) * Float.parseFloat(taxPercentage)) / 100)));
        } else {
            DiscountedPrice = ((Float.parseFloat(product.getPriceVariations().get(0).getDiscounted_price()) + ((Float.parseFloat(product.getPriceVariations().get(0).getDiscounted_price()) * Float.parseFloat(taxPercentage)) / 100)));
        }
        holder.tvPrice.setText(new Session(activity).getData(Constant.CURRENCY) + ApiConfig.StringFormat("" + DiscountedPrice));

        holder.relativeLayout.setOnClickListener(view -> {

            AppCompatActivity activity1 = (AppCompatActivity) activity;
            Fragment fragment = new ProductDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, product.getPriceVariations().get(0).getProduct_id());
            bundle.putString(Constant.FROM, "section");
            bundle.putInt(Constant.VARIANT_POSITION, 0);
            fragment.setArguments(bundle);
            activity1.getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


        });
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class VideoHolder extends RecyclerView.ViewHolder {

        public final ImageView thumbnail;
        public final TextView tvTitle;
        public final TextView tvPrice;
        public final RelativeLayout relativeLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            relativeLayout = itemView.findViewById(R.id.play_layout);

        }


    }
}
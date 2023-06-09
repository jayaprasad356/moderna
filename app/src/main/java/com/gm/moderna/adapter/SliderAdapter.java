package com.gm.moderna.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.gm.moderna.R;
import com.gm.moderna.activity.MainActivity;
import com.gm.moderna.fragment.FullScreenViewFragment;
import com.gm.moderna.fragment.ProductDetailFragment;
import com.gm.moderna.fragment.SubCategoryFragment;
import com.gm.moderna.helper.Constant;
import com.gm.moderna.model.Slider;

public class SliderAdapter extends PagerAdapter {

    final ArrayList<Slider> dataList;
    final Activity activity;
    final int layout;
    final String from;

    public SliderAdapter(ArrayList<Slider> dataList, Activity activity, int layout, String from) {
        this.dataList = dataList;
        this.activity = activity;
        this.layout = layout;
        this.from = from;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View imageLayout = LayoutInflater.from(activity).inflate(layout, view, false);

        assert imageLayout != null;
        ImageView imgSlider = imageLayout.findViewById(R.id.imgSlider);
        CardView lytMain = imageLayout.findViewById(R.id.lytMain);

        final Slider singleItem = dataList.get(position);


        Picasso.get()
                .load(singleItem.getImage())
                .fit()
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imgSlider);
        view.addView(imageLayout, 0);

        lytMain.setOnClickListener(v -> {
            if (from.equalsIgnoreCase("detail")) {

                Fragment fragment = new FullScreenViewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                fragment.setArguments(bundle);

                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

            } else {

                if (singleItem.getType().equals("category")) {

                    Fragment fragment = new SubCategoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.ID, singleItem.getType_id());
                    bundle.putString(Constant.NAME, singleItem.getName());
                    bundle.putString(Constant.FROM, "category");
                    fragment.setArguments(bundle);

                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();


                } else if (singleItem.getType().equals("product")) {

                    Fragment fragment = new ProductDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.ID, singleItem.getType_id());
                    bundle.putString(Constant.FROM, "slider");
                    bundle.putInt(Constant.VARIANT_POSITION, 0);
                    fragment.setArguments(bundle);

                    MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();

                }

            }
        });

        return imageLayout;
    }


    @Override
    public int getCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

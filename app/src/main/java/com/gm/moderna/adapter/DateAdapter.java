package com.gm.moderna.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gm.moderna.R;
import com.gm.moderna.activity.PaymentActivity;
import com.gm.moderna.helper.ApiConfig;
import com.gm.moderna.helper.Constant;
import com.gm.moderna.model.BookingDate;

/**
 * Created by shree1 on 3/16/2017.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.VideoHolder> {

    public final ArrayList<BookingDate> bookingDates;
    public final Activity activity;

    public DateAdapter(Activity activity, ArrayList<BookingDate> bookingDates) {
        this.activity = activity;
        this.bookingDates = bookingDates;
    }

    @Override
    public int getItemCount() {
        return bookingDates.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, final int position) {
        final BookingDate bookingDate = bookingDates.get(position);

        if (Constant.selectedDatePosition == position) {
            PaymentActivity.deliveryDay = bookingDate.getDate() + "-" + ApiConfig.getMonth(Integer.parseInt(bookingDate.getMonth()), activity) + "-" + bookingDate.getYear();
            holder.relativeLyt.setBackgroundResource(R.drawable.selected_date_shadow);
            holder.tvDay.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.tvDate.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.tvMonth.setTextColor(ContextCompat.getColor(activity, R.color.white));
        } else {

            holder.tvDay.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.tvDate.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.tvMonth.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            holder.relativeLyt.setBackgroundResource(R.drawable.date_shadow);
        }

        holder.relativeLyt.setPadding((int) activity.getResources().getDimension(R.dimen._15sdp), (int) activity.getResources().getDimension(R.dimen._15sdp), (int) activity.getResources().getDimension(R.dimen._15sdp), (int) activity.getResources().getDimension(R.dimen._15sdp));


        holder.tvDay.setText(ApiConfig.getDayOfWeek(Integer.parseInt(bookingDate.getDay()), activity));
        holder.tvDate.setText(bookingDate.getDate());
        holder.tvMonth.setText(ApiConfig.getMonth(Integer.parseInt(bookingDate.getMonth()), activity));

        holder.relativeLyt.setOnClickListener(view -> {
            if (PaymentActivity.adapter != null) {
                if (PaymentActivity.deliveryDay.length() > 0) {
                    Constant.selectedDatePosition = holder.getPosition();
                    notifyDataSetChanged();
                    PaymentActivity.deliveryTime = "";
                    PaymentActivity.adapter.notifyDataSetChanged();
                    PaymentActivity.recyclerView.setAdapter(PaymentActivity.adapter);
                }
            }
        });
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_date, parent, false);
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

        public final TextView tvDate;
        public final TextView tvMonth;
        public final TextView tvDay;
        final CardView relativeLyt;

        public VideoHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvDay = itemView.findViewById(R.id.tvDay);
            relativeLyt = itemView.findViewById(R.id.relativeLyt);
        }


    }
}

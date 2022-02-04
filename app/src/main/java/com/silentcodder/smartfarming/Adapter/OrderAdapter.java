package com.silentcodder.smartfarming.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

import com.silentcodder.smartfarming.Fragment.OrderHistoryViewFragment;
import com.silentcodder.smartfarming.Model.OrderData;
import com.silentcodder.smartfarming.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
    List<OrderData> orderData;

    public OrderAdapter(List<OrderData> orderData) {
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_view,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String Status = orderData.get(position).getStatus();
        long TimeStamp = orderData.get(position).getTimeStamp();
        long Count = orderData.get(position).getProductCount();
        long Total = orderData.get(position).getTotalPrice();
        String OrderId = orderData.get(position).OrderId;

        if (Status.equals("Cancel")){
            holder.mIcon.setImageResource(R.drawable.cancel);
            holder.mStatus.setText("Cancel Order");
            holder.mStatus.setTextColor(Color.RED);
        }else if (Status.equals("Complete")){
            holder.mIcon.setImageResource(R.drawable.correct);
            holder.mStatus.setTextColor(Color.parseColor("#2E8B57"));
            holder.mStatus.setText("Complete Order");
        }
        Date d = new Date(TimeStamp);
        DateFormat dateFormat1 = new SimpleDateFormat("MMM dd , yyyy");
        String Date = dateFormat1.format(d.getTime());
        holder.mCount.setText(String.valueOf(Count) + " Products");
        holder.mTotal.setText("₹ " +String.valueOf(Total));
        holder.mDate.setText(Date);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("OrderId",OrderId);
                Fragment fragment = new OrderHistoryViewFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTotal;
        TextView mDate;
        TextView mCount,mStatus;
        ImageView mIcon;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTotal = itemView.findViewById(R.id.Total);
            mDate = itemView.findViewById(R.id.date);
            mStatus = itemView.findViewById(R.id.status);
            mIcon = itemView.findViewById(R.id.icon);
            mCount = itemView.findViewById(R.id.productCount);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}

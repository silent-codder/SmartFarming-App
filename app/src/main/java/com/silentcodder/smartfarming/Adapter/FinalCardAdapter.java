package com.silentcodder.smartfarming.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silentcodder.smartfarming.Model.CardData;
import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FinalCardAdapter extends RecyclerView.Adapter<FinalCardAdapter.ViewHolder> {

    List<CardData> cardData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    String UserId;
    long ItemCount;
    long TotalPrice,TPrice;
    String  ProductPrice,PPrice;

    public FinalCardAdapter(List<CardData> cardData) {
        this.cardData = cardData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_card_view,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ProductId = cardData.get(position).getProductId();
        ItemCount = cardData.get(position).getItemCount();
        TotalPrice = cardData.get(position).getTotalPrice();
        ProductPrice = cardData.get(position).getMainPrice();
        String TPrice = cardData.get(position).getTotalPrices();
        String CardId = cardData.get(position).CardId;

//        if (!TextUtils.isEmpty(TPrice)){
//
//        }
//        if (!TextUtils.isEmpty(String.valueOf(TotalPrice))){
//
//        }
//        holder.mTotalPrice.setText("₹ " + TPrice);
        holder.mTotalPrice.setText("₹ " +String.valueOf(TotalPrice));
        holder.mCount.setText(String.valueOf(ItemCount));


        firebaseFirestore.collection("Products").document(ProductId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String ProductName = task.getResult().getString("ProductName");
                    String ProductImg = task.getResult().getString("ProductImgUrl");
                    String ProductWeight = task.getResult().getString("Unit");
                    String ProductPrice = task.getResult().getString("Price");

                    holder.mPrice.setText("₹ " +ProductPrice);
                    holder.mWeight.setText(ProductWeight);
                    holder.mProductName.setText(ProductName);
                    Picasso.get().load(ProductImg).into(holder.mProductImg);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mProductName;
        TextView mPrice,mTotalPrice;
        TextView mWeight,mCount;
        ImageView mProductImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.productPrice);
            mProductImg = itemView.findViewById(R.id.productImg);
            mWeight = itemView.findViewById(R.id.productWeight);
            mProductName = itemView.findViewById(R.id.productName);
            mTotalPrice = itemView.findViewById(R.id.totalPrice);
            mCount = itemView.findViewById(R.id.count);
        }
    }
}

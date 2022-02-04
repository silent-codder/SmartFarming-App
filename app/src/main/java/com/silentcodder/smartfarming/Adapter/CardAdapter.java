package com.silentcodder.smartfarming.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<CardData> cardData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    String UserId;
    long ItemCount;
    long TotalPrice,TPrice;
    String  ProductPrice,PPrice;

    public CardAdapter(List<CardData> cardData) {
        this.cardData = cardData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_view,parent,false);
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
        String CardId = cardData.get(position).CardId;

        holder.mCount.setText(String.valueOf(ItemCount));
        holder.mTotalPrice.setText("₹ " +String.valueOf(TotalPrice));

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

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users").document(UserId)
                        .collection("Card").document(CardId).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    cardData.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,cardData.size());
                                }
                            }
                        });
            }
        });

        holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer add = Integer.valueOf(holder.mCount.getText().toString()) +1;
                holder.mCount.setText(add.toString());
                ProductPrice = cardData.get(position).getMainPrice();
                int price = Integer.valueOf(ProductPrice) * add;
                holder.mTotalPrice.setText("₹ "+String.valueOf(price));

                ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage("Updating");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                HashMap<String,Object> map = new HashMap<>();
                map.put("ProductId",ProductId);
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("ItemCount",add);
                map.put("TotalPrice",price);
                map.put("MainPrice",ProductPrice);

                firebaseFirestore.collection("Users").document(UserId).collection("Card")
                        .document(ProductId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                    }
                });
            }


        });

        holder.mBtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(holder.mCount.getText().toString())>1){
                    Integer add = Integer.valueOf(holder.mCount.getText().toString()) - 1;
                    holder.mCount.setText(add.toString());
                    ProductPrice = cardData.get(position).getMainPrice();
                    int price = Integer.valueOf(ProductPrice) * add;
                    holder.mTotalPrice.setText("₹ "+String.valueOf(price));

                    ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("Updating");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    HashMap<String,Object> map = new HashMap<>();
                    map.put("ProductId",ProductId);
                    map.put("TimeStamp",System.currentTimeMillis());
                    map.put("ItemCount",add);
                    map.put("TotalPrice",price);
                    map.put("MainPrice",ProductPrice);

                    firebaseFirestore.collection("Users").document(UserId).collection("Card")
                            .document(ProductId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                        }
                    });
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
        ImageView mBtnMinus,mBtnAdd,mBtnDelete;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.productPrice);
            mProductImg = itemView.findViewById(R.id.productImg);
            mWeight = itemView.findViewById(R.id.productWeight);
            mProductName = itemView.findViewById(R.id.productName);
            mTotalPrice = itemView.findViewById(R.id.totalPrice);
            mCount = itemView.findViewById(R.id.count);
            mBtnMinus = itemView.findViewById(R.id.btnMinus);
            mBtnAdd = itemView.findViewById(R.id.btnAdd);
            mBtnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

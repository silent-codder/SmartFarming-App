package com.silentcodder.smartfarming.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silentcodder.smartfarming.Model.ProductData;
import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{
    List<ProductData> productData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId,ProductId;
    int count=1,totalCount;
    String ProductPrice;
    ImageView mBtnMinus,mBtnAdd;
    TextView mCount,mPriceCount;

    Button mBtnAddToCart;

    TextView mProductName,mProductPrice,mProductWeight;
    ImageView mProductImg;

    public ProductListAdapter(List<ProductData> productData) {
        this.productData = productData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ProductName = productData.get(position).getProductName();
        String Price = productData.get(position).getPrice();
        String Weight = productData.get(position).getUnit();
        String ProductImg = productData.get(position).getProductImgUrl();
        String ProductId = productData.get(position).ProductId;

        Picasso.get().load(ProductImg).into(holder.mProductImg);
        holder.mProductName.setText(ProductName);
        holder.mWeight.setText(Weight);
        holder.mPrice.setText("₹ "+Price);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.view_product_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mProductName = dialog.findViewById(R.id.productName);
                mProductPrice = dialog.findViewById(R.id.productPrice);
                mProductWeight = dialog.findViewById(R.id.productWeight);
                mProductImg = dialog.findViewById(R.id.productImg);
                mBtnAddToCart = dialog.findViewById(R.id.btnAddCard);
                mCount = dialog.findViewById(R.id.count);
                mPriceCount = dialog.findViewById(R.id.priceCount);
                mBtnMinus = dialog.findViewById(R.id.btnMinus);
                mBtnAdd = dialog.findViewById(R.id.btnAdd);

                Picasso.get().load(ProductImg).into(mProductImg);
                mProductName.setText(ProductName);
                mProductPrice.setText("₹ "+Price);
                mProductWeight.setText(Weight);
                mPriceCount.setText("₹ "+Price);
                TextView textView = dialog.findViewById(R.id.PriceCount);
                textView.setText(Price);

                totalCount = Integer.valueOf(Price);

                //button add product count
                mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = dialog.findViewById(R.id.Count);
                        count+=1;
                        int price = Integer.valueOf(Price);
                        totalCount = price * count;
                        mCount.setText(String.valueOf(count));
                        textView.setText("x "+count);
                        mPriceCount.setText("₹ "+String.valueOf(totalCount));
                    }
                });

                //button minus product count
                mBtnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (count>1){
                            TextView textView = dialog.findViewById(R.id.Count);
                            count--;
                            int price = Integer.valueOf(Price);
                            textView.setText("x "+count);
                            totalCount = totalCount - price;
                            mPriceCount.setText("₹ "+String.valueOf(totalCount));
                            mCount.setText(String.valueOf(count));
                        }
                    }
                });

                //button add to card
                mBtnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ProductId",ProductId);
                        map.put("TimeStamp",System.currentTimeMillis());
                        map.put("ItemCount",count);
                        map.put("TotalPrice",totalCount);
                        map.put("MainPrice",Price);

                        firebaseFirestore.collection("Users").document(UserId).collection("Card")
                                .document(ProductId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Dialog dialog2 = new Dialog(v.getContext());
                                    dialog2.setContentView(R.layout.add_card_dialog);
                                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog2.show();
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            dialog2.dismiss();
                                        }
                                    },1000);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mProductName;
        TextView mPrice;
        TextView mWeight;
        ImageView mProductImg;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.productPrice);
            mProductImg = itemView.findViewById(R.id.productImg);
            mWeight = itemView.findViewById(R.id.productWeight);
            mProductName = itemView.findViewById(R.id.productName);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}

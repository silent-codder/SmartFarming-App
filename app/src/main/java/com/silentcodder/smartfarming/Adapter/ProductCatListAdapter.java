package com.silentcodder.smartfarming.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.silentcodder.smartfarming.Model.ProductData;
import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductCatListAdapter extends RecyclerView.Adapter<ProductCatListAdapter.ViewHolder>{
    List<ProductData> productData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    String UserId;
    String ProductId;
    int count=1,totalCount;
    String ProductPrice;
    ImageView mBtnMinus,mBtnAdd;
    TextView mCount,mPriceCount;

    Button mBtnAddToCart;

    TextView mProductName,mProductPrice,mProductWeight;
    ImageView mProductImg;

    public ProductCatListAdapter(List<ProductData> productData) {
        this.productData = productData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cat_view,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        context = parent.getContext();
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

        holder.mUnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mLike.setVisibility(View.VISIBLE);
                holder.mUnLike.setVisibility(View.GONE);

                HashMap<String,Object> map = new HashMap<>();
                map.put("TimeStamp",System.currentTimeMillis());
                firebaseFirestore.collection("Products").document(ProductId).collection("Likes").document(UserId).set(map);
            }
        });

        holder.mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mUnLike.setVisibility(View.VISIBLE);
                holder.mLike.setVisibility(View.GONE);
                firebaseFirestore.collection("Products").document(ProductId).collection("Likes").document(UserId).delete();
            }
        });

        firebaseFirestore.collection("Products").document(ProductId).collection("Likes").document(UserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
                            holder.mLike.setVisibility(View.VISIBLE);
                        }
                    }
                });

//        holder.mProductImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                Fragment fragment = new ProductViewFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("ProductId",ProductId);
//                fragment.setArguments(bundle);
//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
//            }
//        });
        holder.mBtnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                Fragment fragment = new ProductViewFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("ProductId",ProductId);
//                fragment.setArguments(bundle);
//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();

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

        Random random = new Random();
        double randomValue = 3.5 + (5.0 - 3.5) * random.nextDouble();
        holder.mRating.setText(String.valueOf(randomValue));
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mProductName,mRating;
        TextView mPrice;
        TextView mWeight;
        ImageView mProductImg;
        ImageView mUnLike;
        ImageView mLike;
        Button mBtnAddToCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPrice = itemView.findViewById(R.id.productPrice);
            mProductImg = itemView.findViewById(R.id.productImg);
            mWeight = itemView.findViewById(R.id.productWeight);
            mProductName = itemView.findViewById(R.id.productName);
            mUnLike = itemView.findViewById(R.id.unlike);
            mLike = itemView.findViewById(R.id.like);
            mBtnAddToCard = itemView.findViewById(R.id.btnAddCard);
            mRating = itemView.findViewById(R.id.rating);
        }
    }
}

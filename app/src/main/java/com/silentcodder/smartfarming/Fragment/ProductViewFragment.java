package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProductViewFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId,ProductId;

    ImageView mBtnMinus,mBtnAdd;
    TextView mCount,mPriceCount;

    Button mBtnAddToCart;

    TextView mProductName,mProductPrice,mProductWeight;
    CircleImageView mProductImg;
    int count=1,totalCount;
    String ProductPrice;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_view, container, false);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            ProductId = bundle.getString("ProductId");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        mProductName = view.findViewById(R.id.productName);
        mProductPrice = view.findViewById(R.id.productPrice);
        mProductWeight = view.findViewById(R.id.productWeight);
        mProductImg = view.findViewById(R.id.productImg);
        mBtnAddToCart = view.findViewById(R.id.btnAddCard);
        mCount = view.findViewById(R.id.count);
        mPriceCount = view.findViewById(R.id.priceCount);
        mBtnMinus = view.findViewById(R.id.btnMinus);
        mBtnAdd = view.findViewById(R.id.btnAdd);

        firebaseFirestore.collection("Products").document(ProductId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String ProductName = task.getResult().getString("ProductName");
                            ProductPrice = task.getResult().getString("Price");
                            String Weight = task.getResult().getString("Unit");
                            String ProductImg = task.getResult().getString("ProductImgUrl");

                            Picasso.get().load(ProductImg).into(mProductImg);
                            mProductName.setText(ProductName);
                            mProductPrice.setText("₹ "+ProductPrice);
                            mProductWeight.setText(Weight);
                            mPriceCount.setText("₹ "+ProductPrice);
                            TextView textView = view.findViewById(R.id.PriceCount);
                            textView.setText(ProductPrice);

                            totalCount = Integer.valueOf(ProductPrice);
                        }
                    }
                });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                TextView textView = view.findViewById(R.id.Count);
                count+=1;
                int price = Integer.valueOf(ProductPrice);
                totalCount = price * count;
                mCount.setText(String.valueOf(count));
                textView.setText("x "+count);
                mPriceCount.setText("₹ "+String.valueOf(totalCount));
            }
        });

        mBtnMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (count>1){
                    TextView textView = view.findViewById(R.id.Count);
                    count--;
                    int price = Integer.valueOf(ProductPrice);
                    textView.setText("x "+count);
                    totalCount = totalCount - price;
                    mPriceCount.setText("₹ "+String.valueOf(totalCount));
                    mCount.setText(String.valueOf(count));
                }
            }
        });

        mBtnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("ProductId",ProductId);
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("ItemCount",count);
                map.put("TotalPrice",totalCount);
                map.put("MainPrice",ProductPrice);

                firebaseFirestore.collection("Users").document(UserId).collection("Card")
                        .document(ProductId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.add_card_dialog);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                            Button btnContinue = dialog.findViewById(R.id.btnContinue);
                            btnContinue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Fragment fragment = new HomeFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }


}
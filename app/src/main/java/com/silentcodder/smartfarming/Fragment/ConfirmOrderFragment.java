package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.silentcodder.smartfarming.Adapter.FinalCardAdapter;
import com.silentcodder.smartfarming.Model.CardData;
import com.silentcodder.smartfarming.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

public class ConfirmOrderFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    TextView mBtnEditCard;
    TextView mTotalPrice,mDeliveryCharge;
    Button mBtnOrderNow;
    ProgressBar progressBar;
    int Total,ProductCount;
    RadioButton mBtnHome,mBtnSelf;
    String Service;
    RecyclerView recyclerView;
    List<CardData> cardData;
    FinalCardAdapter finalCardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.loader);
        mBtnOrderNow = view.findViewById(R.id.btnOrderNow);
        mTotalPrice = view.findViewById(R.id.totalPrice);
        mBtnEditCard = view.findViewById(R.id.btnEditCard);
        mDeliveryCharge = view.findViewById(R.id.deliveryCharge);
        recyclerView = view.findViewById(R.id.recycleView);
        mBtnHome = view.findViewById(R.id.homeDelivery);
        mBtnSelf = view.findViewById(R.id.selfPickup);
        UserId = firebaseAuth.getCurrentUser().getUid();

        cardData = new ArrayList<>();
        finalCardAdapter = new FinalCardAdapter(cardData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(finalCardAdapter);

        Service = "Self Service";

        firebaseFirestore.collection("Users")
                .document(UserId).collection("Card").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String CardId = doc.getDocument().getId();
                        CardData mData = doc.getDocument().toObject(CardData.class).withId(CardId);
                        cardData.add(mData);
                        finalCardAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        firebaseFirestore.collection("Users").document(UserId).collection("Card")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        Total = 00;
                        ProductCount = value.size();
                        for (DocumentChange doc : value.getDocumentChanges()){
                            Total = Total + Integer.valueOf(doc.getDocument().get("TotalPrice").toString());
                            Log.d(TAG, "onEvent: " + Total);
                            TextView subTotal = view.findViewById(R.id.subTotalPrice);
                            subTotal.setText("₹ " +Total);
                        }
//                        Total += 50;
                        mTotalPrice.setText("₹ " +Total);
                    }
                });


        mBtnEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CartFragment();
                getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });

        mBtnSelf.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (Service.equals("Home Delivery")){
                    firebaseFirestore.collection("Users").document(UserId).collection("Card")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    Total = 00;
                                    ProductCount = value.size();
                                    for (DocumentChange doc : value.getDocumentChanges()){
                                        Total = Total + Integer.valueOf(doc.getDocument().get("TotalPrice").toString());
                                        Log.d(TAG, "onEvent: " + Total);
                                        TextView subTotal = view.findViewById(R.id.subTotalPrice);
                                        subTotal.setText("₹ " +Total);
                                    }
//                        Total += 50;
                                    mTotalPrice.setText("₹ " +Total);
                                }
                            });
                    mDeliveryCharge.setText("Free");
                    Service = "Self Service";
                }else {
                    Service = "Self Service";
                    mDeliveryCharge.setText("Free");
                }
            }
        });

        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Service = "Home Delivery";
                mDeliveryCharge.setText("₹ 50");
                AddCharge();
            }
        });

        mBtnOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CheckOutFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Total",String.valueOf(Total));
                bundle.putString("Service",Service);
                bundle.putString("Count",String.valueOf(ProductCount));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();

            }
        });

        return view;
    }

    private void AddCharge() {
        firebaseFirestore.collection("Users").document(UserId).collection("Card")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        Total = 00;
                        ProductCount = value.size();
                        for (DocumentChange doc : value.getDocumentChanges()){
                            Total = Total + Integer.valueOf(doc.getDocument().get("TotalPrice").toString());
                            Log.d(TAG, "onEvent: " + Total);
                            TextView subTotal = getView().findViewById(R.id.subTotalPrice);
                            subTotal.setText("₹ " +Total);
                        }
                        Total += 50;
                        mTotalPrice.setText("₹ " +Total);
                    }
                });
    }

}
package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.silentcodder.smartfarming.Adapter.OrderAdapter;
import com.silentcodder.smartfarming.Model.OrderData;
import com.silentcodder.smartfarming.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    List<OrderData> orderData;
    OrderAdapter orderAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;
    RecyclerView recyclerView;
    ExtendedFloatingActionButton mBtnFilter;
    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        textView = view.findViewById(R.id.text);

        mBtnFilter = view.findViewById(R.id.filter);

        mBtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), mBtnFilter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        String status = item.getTitle().toString();
                        if (!status.equals("All Order")){
                            loadData(status);
                        }else {
                            allOrder();
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu

            }
        });

        allOrder();
        return view;
    }

    private void allOrder() {
        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        firebaseFirestore.collection("Orders").whereEqualTo("UserId",UserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int count = value.size();
                        textView.setText(" Order history (" + String.valueOf(count) + ")");
                        if (value.isEmpty()){
                            LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
                            TextView textView = getView().findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);

                        }else {

                            LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
                            TextView textView = getView().findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }

                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String OrderId = doc.getDocument().getId();
                                OrderData mData = doc.getDocument().toObject(OrderData.class).withId(OrderId);
                                orderData.add(mData);
                                orderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void loadData(String status) {

        orderData = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        firebaseFirestore.collection("Orders").whereEqualTo("UserId",UserId).whereEqualTo("Status",status)
                .orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()){
                            LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
                            TextView textView = getView().findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);

                        }else {

                            LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
                            TextView textView = getView().findViewById(R.id.emptyTxt);

                            lottieAnimationView.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }
                        int count = value.size();
                        textView.setText(status + " Order list (" + String.valueOf(count) + ")");
                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String OrderId = doc.getDocument().getId();
                                OrderData mData = doc.getDocument().toObject(OrderData.class).withId(OrderId);
                                orderData.add(mData);
                                orderAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
}
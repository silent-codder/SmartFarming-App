package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.silentcodder.smartfarming.Adapter.FinalCardAdapter;
import com.silentcodder.smartfarming.Model.CardData;
import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistoryViewFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    RecyclerView recyclerView;
    List<CardData> cardData;
    FinalCardAdapter finalCardAdapter;
    String OrderId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history_view, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        UserId = firebaseAuth.getCurrentUser().getUid();

        cardData = new ArrayList<>();
        finalCardAdapter = new FinalCardAdapter(cardData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(finalCardAdapter);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            OrderId = bundle.getString("OrderId");
        }

        firebaseFirestore.collection("Orders")
                .document(OrderId).collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        firebaseFirestore.collection("Orders")
                .document(OrderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String Status = task.getResult().getString("Status");
                    if (Status.equals("Cancel")){
                        TextView BtnCancelOrder = view.findViewById(R.id.btnCancelOrder);
                        BtnCancelOrder.setBackgroundResource(R.drawable.reorder_btn_bg);
                        BtnCancelOrder.setTextColor(Color.RED);
                        BtnCancelOrder.setText("Order cancelled");
                    }else if ( Status.equals("Complete")){
                        TextView BtnCancelOrder = view.findViewById(R.id.btnCancelOrder);
                        BtnCancelOrder.setBackgroundResource(R.drawable.reorder_btn_bg);
                        BtnCancelOrder.setTextColor(Color.parseColor("#FF1B5E20"));
                        BtnCancelOrder.setText("Order completed");
                    }
                }
            }
        });

        TextView BtnCancelOrder = view.findViewById(R.id.btnCancelOrder);
        BtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = BtnCancelOrder.getText().toString();
                if (text.equals("Cancel Order")){
                    HashMap<String ,Object> map = new HashMap<>();
                    map.put("Status","Cancel");
                    map.put("TimeStamp",System.currentTimeMillis());
                    firebaseFirestore.collection("Orders")
                            .document(OrderId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            TextView BtnCancelOrder = view.findViewById(R.id.btnCancelOrder);
                            BtnCancelOrder.setBackgroundResource(R.drawable.reorder_btn_bg);
                            BtnCancelOrder.setTextColor(Color.RED);
                            BtnCancelOrder.setText("Order cancelled");
                            Toast.makeText(getContext(), "Your order is cancel..", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;
    }
}
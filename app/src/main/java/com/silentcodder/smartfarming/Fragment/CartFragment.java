package com.silentcodder.smartfarming.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.silentcodder.smartfarming.Adapter.CardAdapter;
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

public class CartFragment extends Fragment {

    RecyclerView recyclerView;
    List<CardData> cardData;
    CardAdapter cardAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    Button mBtnCheckout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mBtnCheckout = view.findViewById(R.id.btnCheckout);
//        mTotalPrice = view.findViewById(R.id.totalPrice);
        UserId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.recycleView);




        cardData = new ArrayList<>();
        cardAdapter = new CardAdapter(cardData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cardAdapter);


        firebaseFirestore.collection("Users")
                .document(UserId).collection("Card").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.isEmpty()){
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.empty);
                    TextView textView = view.findViewById(R.id.emptyTxt);
                    TextView btnClearCard = view.findViewById(R.id.btnClearCard);

                    btnClearCard.setVisibility(View.GONE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                    Button button = view.findViewById(R.id.btnHome);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment fragment = new HomeFragment();
                            getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
                        }
                    });

                    lottieAnimationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lottieAnimationView.playAnimation();
                        }
                    });
                }else {
                    Button relativeLayout = view.findViewById(R.id.btnCheckout);
                    relativeLayout.setVisibility(View.VISIBLE);
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.empty);
                    TextView textView = view.findViewById(R.id.emptyTxt);

                    Button button = view.findViewById(R.id.btnHome);
                    button.setVisibility(View.GONE);

                    lottieAnimationView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String CardId = doc.getDocument().getId();
                        CardData mData = doc.getDocument().toObject(CardData.class).withId(CardId);
                        cardData.add(mData);
                        cardAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        mBtnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ConfirmOrderFragment();
                getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });

        TextView btnClearCard = view.findViewById(R.id.btnClearCard);
        btnClearCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.clear_cart_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button btnYes = dialog.findViewById(R.id.btnYes);
                TextView btnCancel = dialog.findViewById(R.id.btnCancel);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("Users").document(UserId)
                                .collection("Card").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentChange doc : value.getDocumentChanges()){
                                    String ProductId = doc.getDocument().get("ProductId").toString();
                                    firebaseFirestore.collection("Users").document(UserId)
                                            .collection("Card").document(ProductId).delete();
                                    cardAdapter.notifyDataSetChanged();
                                    clear();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        return view;
    }

    public void clear() {
        int size = cardData.size();
        cardData.clear();
        cardAdapter.notifyItemRangeRemoved(0,size);
        mBtnCheckout.setVisibility(View.GONE);
    }
}
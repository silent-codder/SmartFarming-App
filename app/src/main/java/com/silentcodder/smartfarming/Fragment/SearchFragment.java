package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.silentcodder.smartfarming.Adapter.ProductCatListAdapter;
import com.silentcodder.smartfarming.Model.ProductData;
import com.silentcodder.smartfarming.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;
    EditText mSearch;
    TextView textView;

    RecyclerView recyclerView;
    List<ProductData> productData;
    ProductCatListAdapter productCatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mSearch = view.findViewById(R.id.btnSearch);
        recyclerView = view.findViewById(R.id.searchRecycleView);

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()<1)
                {
                    clear();
                }
                else {
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.empty);
                    TextView textView = view.findViewById(R.id.emptyTxt);
                    lottieAnimationView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    SearchData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void SearchData(String string) {
        productData = new ArrayList<>();
        productCatListAdapter = new ProductCatListAdapter(productData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productCatListAdapter);
        firebaseFirestore.collection("Products").orderBy("ProductName").startAt(string).endAt(string+"\uf9ff" )
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value.isEmpty()){
                            LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                        }

                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String PostId = doc.getDocument().getId();
                                ProductData mData = doc.getDocument().toObject(ProductData.class).withId(PostId);
                                productData.add(mData);
                                productCatListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    public void clear() {
        int size = productData.size();
        productData.clear();
        productCatListAdapter.notifyItemRangeRemoved(0,size);
    }
}
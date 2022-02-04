package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.silentcodder.smartfarming.Adapter.ProductCatListAdapter;
import com.silentcodder.smartfarming.Adapter.ProductListAdapter;
import com.silentcodder.smartfarming.Model.ProductData;
import com.silentcodder.smartfarming.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView,recyclerView2,recyclerView3;
    List<ProductData> productData;
    ProductListAdapter productListAdapter;
    ProductCatListAdapter productCatListAdapter;
    FirebaseFirestore firebaseFirestore;
    ImageView searchView;
    TextView textView1;
    TextView textView2;
    RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView3 = view.findViewById(R.id.searchRecycleView);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        recyclerView2 = view.findViewById(R.id.recycleView2);
        searchView = view.findViewById(R.id.searchView);
        textView1 = view.findViewById(R.id.text);
        textView2 = view.findViewById(R.id.text2);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container,new SearchFragment()).addToBackStack(null).commit();
            }
        });

        productData = new ArrayList<>();
        productListAdapter = new ProductListAdapter(productData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(productListAdapter);

       allCategory();

        TextView AllCategory = view.findViewById(R.id.allCategory);
        TextView HerbicideCategory = view.findViewById(R.id.Herbicide);
        TextView NutrientCategory = view.findViewById(R.id.nutrient);
        TextView FungicidesCategory = view.findViewById(R.id.fungicides);
        TextView InsecticidesCategory = view.findViewById(R.id.insecticides);
        TextView SeedsCategory = view.findViewById(R.id.seeds);

        AllCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //select category
                AllCategory.setBackgroundResource(R.drawable.all_cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.white));

                HerbicideCategory.setBackgroundResource(R.drawable.cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.secondary));
                NutrientCategory.setBackgroundResource(R.drawable.cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.secondary));
                FungicidesCategory.setBackgroundResource(R.drawable.cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                InsecticidesCategory.setBackgroundResource(R.drawable.cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                SeedsCategory.setBackgroundResource(R.drawable.cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.secondary));

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.GONE);
                allCategory();
            }
        });

        HerbicideCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select category
                HerbicideCategory.setBackgroundResource(R.drawable.all_cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.white));

                AllCategory.setBackgroundResource(R.drawable.cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.secondary));
                NutrientCategory.setBackgroundResource(R.drawable.cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.secondary));
                FungicidesCategory.setBackgroundResource(R.drawable.cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                InsecticidesCategory.setBackgroundResource(R.drawable.cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                SeedsCategory.setBackgroundResource(R.drawable.cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.secondary));

                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);

                String Category = "Herbicide";

                loadData(Category);
            }
        });

        NutrientCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //select category
                NutrientCategory.setBackgroundResource(R.drawable.all_cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.white));

                HerbicideCategory.setBackgroundResource(R.drawable.cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.secondary));
                AllCategory.setBackgroundResource(R.drawable.cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.secondary));
                FungicidesCategory.setBackgroundResource(R.drawable.cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                InsecticidesCategory.setBackgroundResource(R.drawable.cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                SeedsCategory.setBackgroundResource(R.drawable.cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.secondary));

                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);

                String Category = "Plant Nutrients";

                loadData(Category);
            }
        });

        InsecticidesCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //select category
                InsecticidesCategory.setBackgroundResource(R.drawable.all_cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.white));

                HerbicideCategory.setBackgroundResource(R.drawable.cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.secondary));
                NutrientCategory.setBackgroundResource(R.drawable.cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.secondary));
                FungicidesCategory.setBackgroundResource(R.drawable.cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                AllCategory.setBackgroundResource(R.drawable.cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.secondary));
                SeedsCategory.setBackgroundResource(R.drawable.cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.secondary));

                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);

                String Category = "Insecticides";

                loadData(Category);
            }
        });

        FungicidesCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //select category
                FungicidesCategory.setBackgroundResource(R.drawable.all_cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.white));

                HerbicideCategory.setBackgroundResource(R.drawable.cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.secondary));
                NutrientCategory.setBackgroundResource(R.drawable.cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.secondary));
                AllCategory.setBackgroundResource(R.drawable.cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.secondary));
                InsecticidesCategory.setBackgroundResource(R.drawable.cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                SeedsCategory.setBackgroundResource(R.drawable.cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.secondary));
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);

                String Category = "Fungicides";

                loadData(Category);
            }
        });

        SeedsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //select category
                SeedsCategory.setBackgroundResource(R.drawable.all_cat_bg);
                SeedsCategory.setTextColor(getResources().getColor(R.color.white));

                HerbicideCategory.setBackgroundResource(R.drawable.cat_bg);
                HerbicideCategory.setTextColor(getResources().getColor(R.color.secondary));
                NutrientCategory.setBackgroundResource(R.drawable.cat_bg);
                NutrientCategory.setTextColor(getResources().getColor(R.color.secondary));
                FungicidesCategory.setBackgroundResource(R.drawable.cat_bg);
                FungicidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                InsecticidesCategory.setBackgroundResource(R.drawable.cat_bg);
                InsecticidesCategory.setTextColor(getResources().getColor(R.color.secondary));
                AllCategory.setBackgroundResource(R.drawable.cat_bg);
                AllCategory.setTextColor(getResources().getColor(R.color.secondary));

                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);

                String Category = "Seeds";

                loadData(Category);
            }
        });

        //marquee text
        TextView marquee = view.findViewById(R.id.marquee);
        marquee.setSelected(true);
        return view;
    }

    private void SearchData(String string) {
        productData = new ArrayList<>();
        productCatListAdapter = new ProductCatListAdapter(productData);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView3.setAdapter(productCatListAdapter);
        firebaseFirestore.collection("Products").orderBy("ProductName").startAt(string).endAt(string+"\uf9ff" )
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (!value.isEmpty()){
//                    LottieAnimationView lottieAnimationView = getView().findViewById(R.id.empty);
//                    TextView textView = getView().findViewById(R.id.emptyTxt);
//
//                    lottieAnimationView.setVisibility(View.VISIBLE);
//                    textView.setVisibility(View.VISIBLE);
//                }

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String PostId = doc.getDocument().getId();
                        ProductData mData = doc.getDocument().toObject(ProductData.class).withId(PostId);
                        productData.add(mData);
                        productListAdapter.notifyDataSetChanged();
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

    private void allCategory() {
        firebaseFirestore.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int count = value.size();
                TextView countTxt = getView().findViewById(R.id.popularText);
                countTxt.setText("Top Products (" + String.valueOf(count) + ")" );

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String PostId = doc.getDocument().getId();
                        ProductData mData = doc.getDocument().toObject(ProductData.class).withId(PostId);
                        productData.add(mData);
                        productListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void loadData(String category) {
        productData = new ArrayList<>();
        productCatListAdapter = new ProductCatListAdapter(productData);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setAdapter(productCatListAdapter);

        firebaseFirestore.collection("Products").whereEqualTo("Category",category)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int count = value.size();
                TextView countTxt = getView().findViewById(R.id.popularText);
                countTxt.setText("Top Products (" + String.valueOf(count) + ")" );

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Are you sure to exit ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                getActivity().moveTaskToBack(true);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
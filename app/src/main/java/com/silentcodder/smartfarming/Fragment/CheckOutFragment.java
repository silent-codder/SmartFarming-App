package com.silentcodder.smartfarming.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.silentcodder.smartfarming.Notification.APIService;
import com.silentcodder.smartfarming.Notification.Client;
import com.silentcodder.smartfarming.Notification.Data;
import com.silentcodder.smartfarming.Notification.NotificationSender;
import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CheckOutFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    TextView mTotalPrice;
    TextView mAddress;
    TextView mBtnEditAddress;
    Button mBtnPlaceOrder;
    ProgressBar progressBar;
    int Total,ProductCount;
    int Count,DelCount;
    String Service;
    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName;
    String Token = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mAddress = view.findViewById(R.id.address);
        mBtnEditAddress = view.findViewById(R.id.btnEditAddress);
        mBtnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
        progressBar = view.findViewById(R.id.loader);
        UserId = firebaseAuth.getCurrentUser().getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null){
            Total = Integer.valueOf(bundle.getString("Total"));
            ProductCount = Integer.valueOf(bundle.getString("Count"));
            Service = bundle.getString("Service");
        }

         firebaseFirestore.collection("Users").document(UserId)
                 .collection("Address").document(UserId)
                 .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @SuppressLint("SetTextI18n")
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()){
                     String AddressLine = task.getResult().getString("AddressLine");
                     String City = task.getResult().getString("City");
                     String State = task.getResult().getString("State");
                     String PinCode = task.getResult().getString("PinCode");

                     if (TextUtils.isEmpty(AddressLine)){
                         mAddress.setHint("Please delivery address..");
                         mBtnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 mAddress.setError("Add delivery address");
                             }
                         });

                         mAddress.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 Fragment fragment = new AddressFragment();
                                 getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
                             }
                         });
                     }else {
                         mAddress.setText(AddressLine + ", " + City + ", " + State + ", " + PinCode);
                         mBtnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 placeOrder();
                             }
                         });
                     }
                 }
             }
         });



         mBtnEditAddress.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Fragment fragment = new AddressFragment();
                 getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
             }
         });

//        firebaseFirestore.collection("Users").document(UserId).collection("Card")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        Total = 00;
//                        ProductCount = value.size();
//                        for (DocumentChange doc : value.getDocumentChanges()){
//                            Total = Total + Integer.valueOf(doc.getDocument().get("TotalPrice").toString());
//                            Log.d(TAG, "onEvent: " + Total);
//                        }
//                        Total += 50;
//                    }
//                });



        return view;
    }

    private void placeOrder() {

        String DocId = firebaseFirestore.collection("Orders").document().getId();

        progressBar.setVisibility(View.VISIBLE);
        mBtnPlaceOrder.setVisibility(View.GONE);
        firebaseFirestore.collection("Users").document(UserId)
                .collection("Card").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value,FirebaseFirestoreException error) {
                Count=0;
                DelCount=0;
                if (!value.isEmpty()){
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("UserId",UserId);
                    map.put("TimeStamp",System.currentTimeMillis());
                    map.put("TotalPrice",Total);
                    map.put("Service",Service);
                    map.put("Status","Pending");
                    map.put("Address",mAddress.getText().toString());
                    map.put("ProductCount",ProductCount);
                    Log.d(TAG, "ProductCount: " + ProductCount);


                    firebaseFirestore.collection("Orders").document(DocId).set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    for (DocumentChange doc : value.getDocumentChanges()){
                                        String ItemCount = doc.getDocument().get("ItemCount").toString();
                                        String MainPrice = doc.getDocument().get("MainPrice").toString();
                                        String ProductId = doc.getDocument().get("ProductId").toString();
                                        String TotalPrice = doc.getDocument().get("TotalPrice").toString();

                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("ItemCount",Integer.valueOf(ItemCount));
                                        map.put("MainPrice",MainPrice);
                                        map.put("ProductId",ProductId);
                                        map.put("TotalPrice",Integer.valueOf(TotalPrice));
                                        map.put("TimeStamp",System.currentTimeMillis());

                                        firebaseFirestore.collection("Orders").document(DocId).collection("Products")
                                                .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    Count++;
                                                    progressBar.setVisibility(View.GONE);
                                                    mBtnPlaceOrder.setVisibility(View.VISIBLE);
                                                    Log.d(TAG, "Comp: " +Count + ">=" + ProductCount);
                                                    if (Count>=ProductCount){
                                                        String Title = "New Order !!";
                                                        String Msg = "New ₹ " + Total + " rupees order.";
                                                        HashMap<String,Object> map = new HashMap<>();
                                                        map.put("Msg",Msg);
                                                        map.put("Title",Title);
                                                        map.put("TimeStamp",System.currentTimeMillis());
                                                        map.put("NotificationSender",firebaseAuth.getCurrentUser().getUid());
                                                        map.put("NotificationReceiver",null);
                                                        map.put("Status","Pending");
                                                        firebaseFirestore.collection("Notification").add(map)
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        firebaseFirestore.collection("Tokens").document("hzuMn17FCQS5Ffnls7o8QC6kAbT2")
                                                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()){
                                                                                    String token =task.getResult().getString("token");
                                                                                    sendNotification(token,Title,Msg);
                                                                                    getFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment())
                                                                                            .commit();
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                });
                                                        Dialog dialog = new Dialog(getContext());
                                                        dialog.setContentView(R.layout.order_confirm_dialog);
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
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }


    private void sendNotification(String token, String title, String msg) {
        Data data = new Data(title,msg);
        NotificationSender notificationSender = new NotificationSender(data,token);

        APIService apiService = Client.getRetrofit(fcmUrl).create(APIService.class);

        apiService.sendNotification(notificationSender).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
package com.silentcodder.smartfarming.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.silentcodder.smartfarming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class AddressFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    TextView mAddressLine,mCity,mState,mPinCode;
    Button mBtnAddAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();

        mAddressLine = view.findViewById(R.id.addressLine);
        mCity = view.findViewById(R.id.city);
        mState = view.findViewById(R.id.state);
        mPinCode = view.findViewById(R.id.pinCode);
        mBtnAddAddress = view.findViewById(R.id.btnAddAddress);

        firebaseFirestore.collection("Users").document(UserId)
                .collection("Address").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String Address = task.getResult().getString("AddressLine");
                            String City = task.getResult().getString("City");
                            String State = task.getResult().getString("State");
                            String PinCode = task.getResult().getString("PinCode");

                            mAddressLine.setText(Address);
                            mCity.setText(City);
                            mState.setText(State);
                            mPinCode.setText(PinCode);
                        }
                    }
                });

        mBtnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Address = mAddressLine.getText().toString();
                String City = mCity.getText().toString();
                String State = mState.getText().toString();
                String PinCode = mPinCode.getText().toString();

                if (TextUtils.isEmpty(Address) && TextUtils.isEmpty(City)&&TextUtils.isEmpty(State)&&TextUtils.isEmpty(PinCode)){
                    Toast toast = Toast.makeText(getContext(), "Please fill all field", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("AddressLine",Address);
                    map.put("City",City);
                    map.put("State",State);
                    map.put("PinCode",PinCode.toString());

                    firebaseFirestore.collection("Users").document(UserId)
                            .collection("Address").document(UserId).set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast toast = Toast.makeText(getContext(), "Your Address Add Successfully.", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER,0,0);
                                        toast.show();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }
}
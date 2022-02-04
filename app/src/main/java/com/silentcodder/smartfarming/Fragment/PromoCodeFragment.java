package com.silentcodder.smartfarming.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.silentcodder.smartfarming.R;


public class PromoCodeFragment extends Fragment {

    Button mBtnApply;
    EditText mCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_code, container, false);

        mBtnApply = view.findViewById(R.id.btnApply);
        mCode = view.findViewById(R.id.couponCode);

        mBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Code = mCode.getText().toString();
                if (!TextUtils.isEmpty(Code)){
                    Toast toast = Toast.makeText(getContext(), "Invalid Code", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    mCode.setText("");
                }
            }
        });

        return view;
    }
}
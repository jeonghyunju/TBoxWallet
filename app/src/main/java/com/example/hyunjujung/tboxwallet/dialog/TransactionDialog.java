package com.example.hyunjujung.tboxwallet.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.hyunjujung.tboxwallet.R;
import com.victor.loading.newton.NewtonCradleLoading;

public class TransactionDialog extends DialogFragment {

    NewtonCradleLoading newtonLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_dialog, container, false);
        newtonLoading = view.findViewById(R.id.newtonLoading);
        getDialog().setCancelable(false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().setCanceledOnTouchOutside(false);
        newtonLoading.start();
        return view;
    }

}

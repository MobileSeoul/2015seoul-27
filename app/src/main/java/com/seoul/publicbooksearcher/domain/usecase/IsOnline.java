package com.seoul.publicbooksearcher.domain.usecase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IsOnline implements UseCase<Boolean, Void> {

    private Context context;

    public IsOnline(Context context){
        this.context = context;
    }

    @Override
    public Boolean execute(Void arg) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

package com.seoul.publicbooksearcher.domain.async_usecase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.seoul.publicbooksearcher.domain.exception.CantNotKnowLocationException;
import com.seoul.publicbooksearcher.domain.exception.NotGpsSettingsException;
import com.seoul.publicbooksearcher.presentation.AsyncUseCaseListener;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class SortLibraries implements AsyncUseCase<Void>{

    private LocationManager locationManager;
    private Context context;
    private AsyncUseCaseListener asyncUseCaseListener;
    private Timer timer = new Timer();
    private final static String TAG = SortLibraries.class.getName();

    public SortLibraries(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void execute(Void arg, final AsyncUseCaseListener asyncUseCaseListener) {
        this.asyncUseCaseListener = asyncUseCaseListener;
        this.isFound = false;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        asyncUseCaseListener.onBefore(null);
        if (isGPSEnabled) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
            Log.i(TAG, "request location with GPS !!");

            timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        if (!isFound && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener, Looper.getMainLooper());
                            Log.i(TAG, "request location with Network !!");
                        }
                        else
                            asyncUseCaseListener.onError(new CantNotKnowLocationException());
                    } catch (Exception e) {
                        asyncUseCaseListener.onError(new CantNotKnowLocationException());
                    }
                }
            },3000);
        }else{
            asyncUseCaseListener.onError(new NotGpsSettingsException());
        }
    }

    private boolean isFound = false;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    private class GpsLocationListener implements LocationListener {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}

        public void onLocationChanged(Location location) {
            notifyAndRemoveUpdatesIfNotFound(location, this, "current location requested with GPS !!");
        }
    }

    private NetworkLocationListener networkLocationListener = new NetworkLocationListener();
    private class NetworkLocationListener implements LocationListener{
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onLocationChanged(Location location) {
            notifyAndRemoveUpdatesIfNotFound(location, this,"current location requested with Network !!");
        }
    }

    private void notifyAndRemoveUpdatesIfNotFound(Location location, LocationListener locationListener, String log){
        if(!isFound) {
            Log.i(TAG, log);
            notifyAndRemoveUpdates(new com.seoul.publicbooksearcher.domain.Location(location.getLatitude(), location.getLongitude()), locationListener);
            isFound = true;
        }
    }

    private void notifyAndRemoveUpdates(com.seoul.publicbooksearcher.domain.Location location, LocationListener locationListener){
        asyncUseCaseListener.onAfter(location);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
    }
}


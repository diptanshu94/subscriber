package com.yourcompany.subscriber;

/**
 * Created by Diptanshu on 7/25/2017.
 */
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {
    private static String LOG_TAG = "LocationService";
    private IBinder mBinder = new LocationServiceBinder();
    private static long frequency = 5000L;
    private Timer timer = new Timer();
    private boolean isRunning;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        isRunning = true;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        timer.scheduleAtFixedRate(locationTimerTask, 0, LocationService.frequency);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in OnBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
        if(timer != null) {
            timer.cancel();
        }
        isRunning = false;
    }

    public class LocationServiceBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    TimerTask locationTimerTask = new TimerTask() {
        @Override
        public void run() {
            fetchLocation();
            putLocation();
        }
    };

    private void putLocation() {
        float lat = preferences.getFloat("lat", 50);
        float lng = preferences.getFloat("long", 50);
        Log.v(LOG_TAG, "Lat: " + lat);
        Log.v(LOG_TAG, "Long: " + lng);
    }

    private void fetchLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            SharedPreferences.Editor preferenceEditor = preferences.edit();
                            preferenceEditor.putFloat("lat", (float)location.getLatitude());
                            preferenceEditor.putFloat("long", (float)location.getLongitude());
                            preferenceEditor.commit();
                        }
                    }
                });
    }
}
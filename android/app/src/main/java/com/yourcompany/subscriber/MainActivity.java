package com.yourcompany.subscriber;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  boolean locationServiceBound = false;
  LocationService locationService;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, LocationService.class);
    bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
  }

  private ServiceConnection locationServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      LocationService.LocationServiceBinder locationServiceBinder = (LocationService.LocationServiceBinder) iBinder;
      locationService = locationServiceBinder.getService();
      locationServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      locationServiceBound = false;
    }
  };
}

package com.yourcompany.subscriber;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.view.FlutterMain;

public class MainActivity extends FlutterActivity {
  boolean locationServiceBound = false;
  LocationService locationService;
  private static final String MAPS_CHANNEL = "com.locationapp/maps";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), MAPS_CHANNEL).setMethodCallHandler(
            new MethodChannel.MethodCallHandler() {
              @Override
              public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (call.method.equals("launchMaps")) {
                  Intent i = new Intent(MainActivity.this, MapsActivity.class);
                  i.putExtra("lat", (double)call.argument("lat"));
                  i.putExtra("long", (double)call.argument("long"));
                  getFlutterView().pushRoute("/map-page");
                  startActivityForResult(i,0);
                } else {
                  result.notImplemented();
                }
              }
            }
    );
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    getFlutterView().popRoute();
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

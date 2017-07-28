package com.yourcompany.subscriber;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StringCodec;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    boolean locationServiceBound = false;
    LocationService locationService;
    private static final String MAPS_CHANNEL = "map_channel";
    private FlutterView flutterView;
    private BasicMessageChannel messageChannel;

    private String[] getArgsFromIntent(Intent intent) {
        // Before adding more entries to this list, consider that arbitrary
        // Android applications can generate intents with extra data and that
        // there are many security-sensitive args in the binary.
        ArrayList<String> args = new ArrayList<String>();
        if (intent.getBooleanExtra("trace-startup", false)) {
            args.add("--trace-startup");
        }
        if (intent.getBooleanExtra("start-paused", false)) {
            args.add("--start-paused");
        }
        if (intent.getBooleanExtra("enable-dart-profiling", false)) {
            args.add("--enable-dart-profiling");
        }
        if (!args.isEmpty()) {
            String[] argsArray = new String[args.size()];
            return args.toArray(argsArray);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] args = getArgsFromIntent(getIntent());
        FlutterMain.ensureInitializationComplete(getApplicationContext(), args);
        setContentView(R.layout.maps);
        getSupportActionBar().hide();

        flutterView = (FlutterView) findViewById(R.id.flutter_view);
        flutterView.runFromBundle(FlutterMain.findAppBundlePath(getApplicationContext()), null);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        messageChannel = new BasicMessageChannel<>(flutterView, MAPS_CHANNEL, StringCodec.INSTANCE);
        messageChannel.
                setMessageHandler(new BasicMessageChannel.MessageHandler<String>() {
                    @Override
                    public void onMessage(String s, BasicMessageChannel.Reply<String> reply) {
                        showMap();
                        reply.reply("");
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0))).title("Marker"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(59, 50))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showMap(){
        Log.v("mapsactivity", "FlutterIncrementCalled");
        Toast.makeText(MainActivity.this, "This is my Toast message!",
                Toast.LENGTH_LONG).show();
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

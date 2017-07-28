package com.yourcompany.subscriber;

import android.content.Intent;
import android.os.Bundle;
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
import io.flutter.plugin.common.StringCodec;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterView;

/**
 * Created by Diptanshu on 7/27/2017.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FlutterView flutterView;
    private String CHANNEL = "map_channel";
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

        flutterView = (FlutterView) findViewById(R.id.flutter_view);
        //flutterView.pushRoute("/map-page");
        flutterView.runFromBundle(FlutterMain.findAppBundlePath(getApplicationContext()), null);
        flutterView.pushRoute("/map-page");
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        FlutterActivity flutterActivity = new FlutterActivity();
        FlutterView flutterView = flutterActivity.createFlutterView(MainActivity);
        messageChannel = new BasicMessageChannel<>(flutterView, CHANNEL, StringCodec.INSTANCE);
        messageChannel.
                setMessageHandler(new BasicMessageChannel.MessageHandler<String>() {
                    @Override
                    public void onMessage(String s, BasicMessageChannel.Reply<String> reply) {
                        onFlutterIncrement();
                        reply.reply("");
                    }
                });

    }

    private void onFlutterIncrement() {
        Log.v("mapsactivity", "FlutterIncrementCalled");
        Toast.makeText(MapsActivity.this, "This is my Toast message!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        flutterView.popRoute();
        flutterView.destroy();
        super.onDestroy();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0))).title("Marker"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("long", 0)))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
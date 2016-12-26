package com.example.jula.pms_lab5_1;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    Button btnStart;
    Button btnStop;
    BroadcastReceiver broadcastReceiver;
    TextView tvStarted;
    TextView tvLongitude;
    TextView tvLatitude;
    TextView tvAttitude;
    TextView tvElapsedTime;
    TextView tvCity;
    IntentFilter intentFilter;
    Date startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.buttonStart);
        btnStop = (Button) findViewById(R.id.buttonStop);
        tvStarted = (TextView) findViewById(R.id.textViewStart);
        tvLongitude = (TextView) findViewById(R.id.textViewLong);
        tvLatitude = (TextView) findViewById(R.id.textViewLat);
        tvAttitude = (TextView) findViewById(R.id.textViewAt);
        tvElapsedTime = (TextView) findViewById(R.id.textViewElaps);
        tvCity = (TextView) findViewById(R.id.textViewCity);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Intent intent = new Intent("com.example.jula.pms_lab5_1.android.action.broadcast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, pendingIntent);

        intentFilter = new IntentFilter("com.example.jula.pms_lab5_1.android.action.broadcast");

        broadcastReceiver = new LocationReceiver();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver(broadcastReceiver, intentFilter);
                startTime = new Date();

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException ex) {

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT);
                }
                break;
            }
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT);
                }
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {

        }
    }

    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Do this when the system sends the intent
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Bundle b = intent.getExtras();
            Location loc = (Location) b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
            if (loc != null) {
                tvStarted.setText(timeFormat.format(startTime));
                tvLongitude.setText(String.format("%.7f", loc.getLongitude()));
                tvLatitude.setText(String.format("%.7f", loc.getLatitude()));
                tvAttitude.setText(String.format("%.7f", loc.getAltitude()));
                tvElapsedTime.setText(String.valueOf((new Date().getTime() - startTime.getTime()) / 1000));
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (addresses.size() > 0)
                        tvCity.setText(String.format("%s, %s", addresses.get(0).getLocality(),
                                addresses.get(0).getFeatureName()));
                } catch (IOException ignored) {}
            }
        }
    }
}








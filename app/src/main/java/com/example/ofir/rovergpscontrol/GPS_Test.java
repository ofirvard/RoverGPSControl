package com.example.ofir.rovergpscontrol;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GPS_Test extends AppCompatActivity
{
    final int REQUEST_CAMERA_PERMISSION = 200;
    TextView info;
    GPSTracker gpsTracker;
    final Handler handler = new Handler();
    final Runnable rerouteing = new Runnable()
    {
        @Override
        public void run()
        {
            Location location = gpsTracker.getLocation();
            String text = "Current Info:\n" +
                    "Latitude  : " + location.getLatitude() + "\n" +
                    "Longitude : " + location.getLongitude();
            info.setText(text);
            handler.postDelayed(rerouteing, 1000);//1 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        info = findViewById(R.id.info);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}
                , REQUEST_CAMERA_PERMISSION);
    }

    public void start(View view)
    {
        gpsTracker = new GPSTracker(getApplicationContext());
        handler.post(rerouteing);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                // close the app
                Toast.makeText(this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}

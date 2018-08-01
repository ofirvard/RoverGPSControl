package com.example.ofir.rovergpscontrol;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements BearingToNorthProvider.ChangeEventListener
{
    static final int REQUEST_CAMERA_PERMISSION = 200;
    static final String END_TRANSMISSION = "END";
    TextView info;
    SocketClient socketClient;
    GPSTracker gpsTracker;
    double bearingToNorth = 0;
    BearingToNorthProvider mBearingProvider;
    double lat, lon;
    final Handler handler = new Handler();
    final Runnable rerouteing = new Runnable()
    {
        @Override
        public void run()
        {
            if (!socketClient.ready)
                handler.postDelayed(rerouteing, 1000);//1 seconds
            else
            {
                Location locationA = new Location("destination");

                locationA.setLatitude(lat);
                locationA.setLongitude(lon);

                Location locationB = new Location("current");
                double latB = gpsTracker.getLatitude(), lonB = gpsTracker.getLongitude();

                locationB.setLatitude(latB);
                locationB.setLongitude(lonB);

                float distance = locationA.distanceTo(locationB);
                float bearing = locationB.bearingTo(locationA);
                float turn = (float) bearingToNorth - bearing;

                String text = "Current Info:\n" +
                        "Destination\n" +
                        "Latitude  : " + lat + "\n" +
                        "Longitude : " + lon + "\n" +
                        "\nCurrent\n" +
                        "Latitude  : " + latB + "\n" +
                        "Longitude : " + lonB + "\n\n" +
                        "Distance  : " + distance + "\n" +
                        "Bearing   : " + bearing + "\n" +
                        "Bearing N : " + bearingToNorth + "\n" +
                        "Turn      : " + turn;
                info.setText(text);

                try
                {
                    if (distance > 1)
                    {
                        DataOutputStream DOS = new DataOutputStream(socketClient.socket.getOutputStream());
                        DOS.writeUTF(Float.toString(turn));
                        handler.postDelayed(rerouteing, 5000);//5 seconds
                    }
                    else
                    {
                        DataOutputStream DOS = new DataOutputStream(socketClient.socket.getOutputStream());
                        DOS.writeUTF(END_TRANSMISSION);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = findViewById(R.id.info);
        mBearingProvider = new BearingToNorthProvider(this);
        mBearingProvider.setChangeEventListener(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}
                , REQUEST_CAMERA_PERMISSION);

        gpsTracker = new GPSTracker(this);
    }

    public void setDst(View view)
    {
        lat = Double.parseDouble(((TextView) findViewById(R.id.lat)).getText().toString());
        lon = Double.parseDouble(((TextView) findViewById(R.id.lon)).getText().toString());

        String text = "Current Info:\n" +
                "Destination\n" +
                "Latitude  : " + lat + "\n" +
                "Longitude : " + lon + "\n";
        info.setText(text);
    }

    public void setHere(View view)
    {
        lat = gpsTracker.getLatitude();
        lon = gpsTracker.getLongitude();

        String text = "Current Info:\n" +
                "Destination\n" +
                "Latitude  : " + lat + "\n" +
                "Longitude : " + lon + "\n";
        info.setText(text);
    }

    public void start(View view)
    {
        socketClient = new SocketClient(((TextView) findViewById(R.id.ip)).getText().toString(),((TextView) findViewById(R.id.port)).getText().toString());
        socketClient.start();
        handler.post(rerouteing);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBearingProvider.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBearingProvider.stop();
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            socketClient.socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBearingChanged(double bearing)
    {
        bearingToNorth = bearing;
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

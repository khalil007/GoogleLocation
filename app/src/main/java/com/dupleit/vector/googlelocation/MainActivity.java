package com.dupleit.vector.googlelocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final String LOG_TAG = "LaurenceTestApp";
    private TextView txtOutput;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        txtOutput = findViewById(R.id.txtOutput);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, location.toString());

        Address loactionAddress;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            loactionAddress = addresses.get(0);
            if(loactionAddress!=null)
            {

                String address = loactionAddress.getAddressLine(0);
                String address1 = loactionAddress.getAddressLine(1);
                String city = loactionAddress.getLocality();
                String state = loactionAddress.getAdminArea();
                String country = loactionAddress.getCountryName();
                String postalCode = loactionAddress.getPostalCode();


                String currentLocation;

                if(!TextUtils.isEmpty(address))
                {
                    currentLocation=address;

                    if (!TextUtils.isEmpty(address1))
                        currentLocation+="\n"+address1;

                    if (!TextUtils.isEmpty(city))
                    {
                        currentLocation+="\n"+city;

                        if (!TextUtils.isEmpty(postalCode))
                            currentLocation+=" - "+postalCode;
                    }
                    else
                    {
                        if (!TextUtils.isEmpty(postalCode))
                            currentLocation+="\n"+postalCode;
                    }

                    if (!TextUtils.isEmpty(state))
                        currentLocation+="\n"+state;

                    if (!TextUtils.isEmpty(country))
                        currentLocation+="\n"+country;

                    Log.d("location",""+currentLocation);
                }

            }
            else
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mLocationRequest!=null){
            //txtOutput.setText(location.toString());
            txtOutput.setText(""+location.getLatitude()+"--"+location.getLongitude());

        }
    }
}
package com.example.ayush.augmentedreality;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private FirebaseAuth mFirebaseAuth;
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    static int mUserIdNo;
    static double distance12, distance23, distance31;
    static double latitude1, longitude1, latitude2, longitude2, latitude3, longitude3;
    private DatabaseReference myFirebaseRef = FirebaseDatabase.getInstance().getReference();
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        assert mFirebaseUser != null;
        mUserId = mFirebaseUser.getUid();
        if (mUserId.equals("Rvev8SzktpWMN4COLHS6yWQOnxQ2")) {
            mUserIdNo = 1;
        }
        if (mUserId.equals("V5GaSfckMkXXjra3Hq3BqXgdzt63")) {
            mUserIdNo = 2;
        }
        if (mUserId.equals("hrm3XLx0FaS9NU2QnmLwfNxa5Lk2")) {
            mUserIdNo = 3;
        }

        if (mFirebaseUser == null) {
            loadLogInView();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void saveToFirebase() {
        Map<String, Object> mLocations = new HashMap<String, Object>();
        mLocations.put("timestamp", mLastUpdateTime);
        Map<String, Double> mCoordinate = new HashMap<String, Double>();
        mCoordinate.put("latitude", mCurrentLocation.getLatitude());
        mCoordinate.put("longitude", mCurrentLocation.getLongitude());
        mLocations.put("location", mCoordinate);
        myFirebaseRef.child("users").child(mUserId).push().setValue(mLocations);
        Toast.makeText(this, "Location Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    Location mCurrentLocation;
    private String mLastUpdateTime;

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date = new Date();
        mLastUpdateTime = dateFormat.format(date);
        saveToFirebase();
        geoDistance();
    }

    private void geoDistance() {
        String UserId1 = "Rvev8SzktpWMN4COLHS6yWQOnxQ2"; //ayush.saarathi@gmail.com
        String UserId2 = "V5GaSfckMkXXjra3Hq3BqXgdzt63"; //iitg.ayush@gmail.com
        String UserId3 = "hrm3XLx0FaS9NU2QnmLwfNxa5Lk2"; //ayushvijay.iitg@gmail.com

        Query queryRef1 = myFirebaseRef.child("users").child(UserId1).orderByChild("timestamp").limitToLast(1);
        queryRef1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map data = (Map) dataSnapshot.getValue();
                Map mCoordinate = (HashMap) data.get("location");
                MainActivity.latitude1 = (double) (mCoordinate.get("latitude"));
                MainActivity.longitude1 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId1", "Lat:" + String.valueOf(MainActivity.latitude1) + " " + "Long:" + String.valueOf(MainActivity.longitude1));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryRef2 = myFirebaseRef.child("users").child(UserId2).orderByChild("timestamp").limitToLast(1);
        queryRef2.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map data = (Map) dataSnapshot.getValue();
                Map mCoordinate = (HashMap) data.get("location");
                MainActivity.latitude2 = (double) (mCoordinate.get("latitude"));
                MainActivity.longitude2 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId2", "Lat:" + String.valueOf(MainActivity.latitude2) + " " + "Long:" + String.valueOf(MainActivity.longitude2));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryRef3 = myFirebaseRef.child("users").child(UserId3).orderByChild("timestamp").limitToLast(1);
        queryRef3.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map data = (Map) dataSnapshot.getValue();
                Map mCoordinate = (HashMap) data.get("location");
                MainActivity.latitude3 = (double) (mCoordinate.get("latitude"));
                MainActivity.longitude3 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId3", "Lat:" + String.valueOf(MainActivity.latitude3) + " " + "Long:" + String.valueOf(MainActivity.longitude3));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("check", String.valueOf(MainActivity.latitude1));

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.distance12 = haversineDistance(MainActivity.latitude1, MainActivity.longitude1, MainActivity.latitude2, MainActivity.longitude2);
                        MainActivity.distance23 = haversineDistance(MainActivity.latitude2, MainActivity.longitude2, MainActivity.latitude3, MainActivity.longitude3);
                        MainActivity.distance31 = haversineDistance(MainActivity.latitude3, MainActivity.longitude3, MainActivity.latitude1, MainActivity.longitude1);
                        Log.d("distance12", String.valueOf(MainActivity.distance12));
                        Log.d("distance23", String.valueOf(MainActivity.distance23));
                        Log.d("distance31", String.valueOf(MainActivity.distance31));
                    }
                },
                10000
        );
    }

    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth
        double latDistance = toRad(lat2 - lat1);
        double lonDistance = toRad(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; //distance in metres
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void newGPSCoordinatesActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main9Activity.class);
        startActivity(intent);
    }

    public void newMapActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
        startActivity(intent);
    }

    public void newDataActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
        startActivity(intent);
    }

    public void newOpenCVActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), OpenCVActivity.class);
        startActivity(intent);
    }
}
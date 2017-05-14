package com.example.ayush.augmentedreality;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Main3Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    Marker currLocationMarker;
    private String mUserId;
    private static final String TAG = "Main3Activity";
    private DatabaseReference myFirebaseRef = FirebaseDatabase.getInstance().getReference();
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main3);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        assert mFirebaseUser != null;
        mUserId = mFirebaseUser.getUid();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        mGoogleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        //  Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.army));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //  Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //  Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    Location mCurrentLocation;

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Pos" + " Lat:" + mCurrentLocation.getLatitude() + " Long:" + mCurrentLocation.getLongitude());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.army));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        // Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
        drawLocations("Rvev8SzktpWMN4COLHS6yWQOnxQ2");
        drawLocations("V5GaSfckMkXXjra3Hq3BqXgdzt63");
        drawLocations("hrm3XLx0FaS9NU2QnmLwfNxa5Lk2");
    }

    private void drawLocations(final String userId) {
        Query queryRef = myFirebaseRef.child("users").child(userId).orderByChild("timestamp").limitToLast(5);
        queryRef.addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map data = (Map) dataSnapshot.getValue();
                String timestamp = (String) data.get("timestamp");
                Map mCoordinate = (HashMap) data.get("location");
                double latitude = (double) (mCoordinate.get("latitude"));
                double longitude = (double) (mCoordinate.get("longitude"));

                LatLng mLatLng = new LatLng(latitude, longitude);

                builder.include(mLatLng);
                bounds = builder.build();

                if (userId.equals("Rvev8SzktpWMN4COLHS6yWQOnxQ2")) {
                    MarkerOptions mMarkerOption = new MarkerOptions()
                            .position(mLatLng)
                            .title(timestamp + " ayush.saarathi")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.measle_blue));
                    Marker mMarker = mGoogleMap.addMarker(mMarkerOption);
                    markerList.add(mMarker);
                }

                if (userId.equals("V5GaSfckMkXXjra3Hq3BqXgdzt63")) {
                    MarkerOptions mMarkerOption = new MarkerOptions()
                            .position(mLatLng)
                            .title(timestamp + " iitg.ayush")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green));
                    Marker mMarker = mGoogleMap.addMarker(mMarkerOption);
                    markerList.add(mMarker);
                }

                if (userId.equals("hrm3XLx0FaS9NU2QnmLwfNxa5Lk2")) {
                    MarkerOptions mMarkerOption = new MarkerOptions()
                            .position(mLatLng)
                            .title(timestamp + " ayushvijay.iitg")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.grey));
                    Marker mMarker = mGoogleMap.addMarker(mMarkerOption);
                    markerList.add(mMarker);
                }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
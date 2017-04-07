package com.example.ayush.augmentedreality;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Date;
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
    double latitude1, longitude1, latitude2, longitude2, latitude3, longitude3;
    private DatabaseReference myFirebaseRef = FirebaseDatabase.getInstance().getReference();
    List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        assert mFirebaseUser != null;
        mUserId = mFirebaseUser.getUid();

        geoDistance();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        mGoogleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_SHORT).show();
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

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Pos" + " Lat:" + mCurrentLocation.getLatitude() + " Long:" + mCurrentLocation.getLongitude());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.army));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
        saveToFirebase();
        drawLocations();
    }

    private void saveToFirebase() {
        Map<String, Object> mLocations = new HashMap<String, Object>();
        mLocations.put("timestamp", mLastUpdateTime);
        Map<String, Double> mCoordinate = new HashMap<String, Double>();
        mCoordinate.put("latitude", mCurrentLocation.getLatitude());
        mCoordinate.put("longitude", mCurrentLocation.getLongitude());
        mLocations.put("location", mCoordinate);
        myFirebaseRef.child("users").child(mUserId).push().setValue(mLocations);
    }

    private void drawLocations() {
        Query queryRef = myFirebaseRef.child("users").child(mUserId).orderByChild("timestamp");
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

                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(mLatLng)
                        .title(timestamp)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.measle_blue));
                Marker mMarker = mGoogleMap.addMarker(mMarkerOption);
                markerList.add(mMarker);

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
                Main3Activity.this.latitude1 = (double) (mCoordinate.get("latitude"));
                Main3Activity.this.longitude1 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId1", "Lat:" + String.valueOf(latitude1) + " " + "Long:" + String.valueOf(longitude1));
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
                Main3Activity.this.latitude2 = (double) (mCoordinate.get("latitude"));
                Main3Activity.this.longitude2 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId2", "Lat:" + String.valueOf(latitude2) + " " + "Long:" + String.valueOf(longitude2));
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
                Main3Activity.this.latitude3 = (double) (mCoordinate.get("latitude"));
                Main3Activity.this.longitude3 = (double) (mCoordinate.get("longitude"));
                Log.d("UserId3", "Lat:" + String.valueOf(latitude3) + " " + "Long:" + String.valueOf(longitude3));
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

        Log.d("check", String.valueOf(latitude1));

        double distance12 = haversineDistance(latitude1, longitude1, latitude2, longitude2);
        double distance23 = haversineDistance(latitude2, longitude2, latitude3, longitude3);
        double distance31 = haversineDistance(latitude3, longitude3, latitude1, longitude1);
        Log.d("distance12", String.valueOf(distance12));
        Log.d("distance23", String.valueOf(distance23));
        Log.d("distance31", String.valueOf(distance31));
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
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
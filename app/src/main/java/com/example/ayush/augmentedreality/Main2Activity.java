package com.example.ayush.augmentedreality;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Main2Activity extends AppCompatActivity {

    private DatabaseReference myFirebaseRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser mFirebaseUser;
    protected EditText latEditText;
    protected EditText longEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        assert mFirebaseUser != null;

    }

    public void generateLocation(View v) {
        latEditText = (EditText) findViewById(R.id.editTextLat);
        longEditText = (EditText) findViewById(R.id.editTextLong);
        final String latitudeStr = latEditText.getText().toString();
        final String longitudeStr = longEditText.getText().toString();

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
            builder.setMessage("Please make sure you enter a latitude and longitude!")
                    .setTitle("Error")
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            final double latitude = Double.parseDouble(latEditText.getText().toString());
            final double longitude = Double.parseDouble(longEditText.getText().toString());
            Map<String, Object> mLocations = new HashMap<String, Object>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            Date date = new Date();
            String mLastUpdateTime = dateFormat.format(date);
            mLocations.put("timestamp", mLastUpdateTime);
            Map<String, Double> mCoordinate = new HashMap<String, Double>();
            mCoordinate.put("latitude", latitude);
            mCoordinate.put("longitude", longitude);
            mLocations.put("location", mCoordinate);
            myFirebaseRef.child("users").child("m7FcyUTqfzbY6JYSVCyiQGzhJq22").push().setValue(mLocations);
            Toast.makeText(this, "Location Shared", Toast.LENGTH_SHORT).show();
        }
    }
}
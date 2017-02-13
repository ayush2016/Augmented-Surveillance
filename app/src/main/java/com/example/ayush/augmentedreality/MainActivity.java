package com.example.ayush.augmentedreality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV not loaded!");
        } else {
            Log.d(TAG,"OpenCV loaded!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            loadLogInView();
        }
    }

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

    public void newActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
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

    public void newAccelerometerActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main5Activity.class);
        startActivity(intent);
    }

    public void newGyroscopeActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main6Activity.class);
        startActivity(intent);
    }

    public void newMagnetometerActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main7Activity.class);
        startActivity(intent);
    }

    public void newScanQRCodeActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main8Activity.class);
        startActivity(intent);
    }

    public void newGenerateQRCodeActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main10Activity.class);
        startActivity(intent);
    }

    public void newFaceDetectionActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), Main11Activity.class);
        startActivity(intent);
    }


    public void newVideoFaceDetectionActivityOnClick(View V) {
        Intent intent = new Intent(getApplicationContext(), FaceTrackerActivity.class);
        startActivity(intent);
    }
}
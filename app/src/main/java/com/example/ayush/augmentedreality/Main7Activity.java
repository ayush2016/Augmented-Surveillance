package com.example.ayush.augmentedreality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Main7Activity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private TextView currentX, currentY, currentZ;
    float[] magnetic_field = {0.0f, 0.0f, 0.0f};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success! we have a magnetometer
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        } else {
            // fail we don't have a magnetometer!
            Toast.makeText(this, "Sorry, you don't have a magnetometer!", Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
    }
    //onResume() register the magnetometer for listening the events

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }
    //onPause() unregister the magnetometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();

        // display the current x,y,z magnetometer values
        displayCurrentValues();

        magnetic_field[0] = event.values[0];
        magnetic_field[1] = event.values[1];
        magnetic_field[2] = event.values[2];

    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z magnetometer values

    public void displayCurrentValues() {
        currentX.setText(Float.toString(magnetic_field[0]));
        currentY.setText(Float.toString(magnetic_field[1]));
        currentZ.setText(Float.toString(magnetic_field[2]));
    }

}

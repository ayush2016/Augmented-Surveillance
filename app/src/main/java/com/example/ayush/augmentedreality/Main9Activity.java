package com.example.ayush.augmentedreality;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Main9Activity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    static double valueAzimuth, valueRoll, valuePitch;

    private static final String TAG = "Main9Activity";
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    static {
        System.loadLibrary("MyLibs");
    }

    JavaCameraView javaCameraView;

    Mat mRgba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main9);
        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view_2);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        //javaCameraView.setMaxFrameSize(320,240);
        javaCameraView.setMaxFrameSize(640, 480);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

  /*
   * event.values[0]: azimuth, rotation around the Z axis.
   * event.values[1]: pitch, rotation around the X axis.
   * event.values[2]: roll, rotation around the Y axis.
   */
        valueAzimuth = Math.toDegrees(event.values[0]);
        valuePitch = Math.toDegrees(event.values[1]);
        valueRoll = Math.toDegrees(event.values[2]);
        Log.i(TAG, "Azimuth, Pitch, Roll Values: " + valueAzimuth + " " + valuePitch + " " + valueRoll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV successfully loaded!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV not loaded!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        }
        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        //OpenCVClass.faceDetection(mRgba.getNativeObjAddr());
        OpenCVClass.humanDetection2(mRgba.getNativeObjAddr(), MainActivity.distance12,
                MainActivity.distance23, MainActivity.distance31, MainActivity.mUserIdNo);
        return mRgba;
    }
}


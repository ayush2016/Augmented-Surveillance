package com.example.ayush.augmentedreality;

/**
 * Created by ayush on 17/3/17.
 */

public class OpenCVClass {
    public native static void faceDetection(long addrRgba);
    public native static void humanDetection(long addrRgba);
}

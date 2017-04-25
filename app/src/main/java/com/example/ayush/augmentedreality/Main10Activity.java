package com.example.ayush.augmentedreality;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Main10Activity extends AppCompatActivity {

    protected EditText latEditText;
    protected EditText longEditText;
    protected ImageView QRCodeImageView;
    protected String QRCode;
    public final static int WIDTH = 500;
    public final static int HEIGHT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main10);
    }

    public void generateLocationQR(View v) {
        latEditText = (EditText) findViewById(R.id.editTextLat);
        longEditText = (EditText) findViewById(R.id.editTextLong);
        final String latitude = latEditText.getText().toString();
        final String longitude = longEditText.getText().toString();

        if (latitude.isEmpty() || longitude.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Main10Activity.this);
            builder.setMessage("Please make sure you enter a latitude and longitude!")
                    .setTitle("Error")
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            getID();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    QRCode = "Latitude: " + latitude + " " + "Longitude: " + longitude;
                    try {
                        synchronized (this) {
                            wait(5000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Bitmap bitmap = null;
                                        bitmap = encodeAsBitmap(getBaseContext(), QRCode);
                                        QRCodeImageView.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    private void getID() {
        QRCodeImageView = (ImageView) findViewById(R.id.qr_code_image);
    }

    Bitmap encodeAsBitmap(Context context, String s) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(s, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(context, R.color.black) : ContextCompat.getColor(context, R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }

}


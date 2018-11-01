package org.smartregister.anc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.smartregister.anc.R;
import org.smartregister.anc.util.Constants;

import java.io.IOException;

public class BarcodeScanActivity extends Activity implements SurfaceHolder.Callback, Detector.Processor<Barcode> {
    private CameraSource cameraSource;
    private SurfaceView cameraPreview;
    private String TAG = BarcodeScanActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        cameraPreview = findViewById(R.id.cameraPreview);
        createCameraSource();
    }

    public void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 400)
                .setRequestedFps(15.0f)
                .build();
        cameraPreview.getHolder().addCallback(this);
        barcodeDetector.setProcessor(this);
    }


    public void closeBarcodeActivity(SparseArray<Barcode> sparseArray) {
        Intent intent = new Intent();
        if (sparseArray != null) {
            intent.putExtra(Constants.BARCODE.BARCODE_KEY, sparseArray.valueAt(0));
        }
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            cameraSource.start();
        } catch (SecurityException se) {
            Log.e(TAG, "Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Todo
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    @Override
    public void release() {
        //Todo
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodeSparseArray = detections.getDetectedItems();
        if (barcodeSparseArray.size() > 0) {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(100);
            closeBarcodeActivity(barcodeSparseArray);
        }
    }
}

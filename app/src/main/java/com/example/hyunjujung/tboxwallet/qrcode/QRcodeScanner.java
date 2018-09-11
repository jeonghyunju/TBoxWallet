package com.example.hyunjujung.tboxwallet.qrcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.hyunjujung.tboxwallet.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final String TAG = "QRcodeScanner";
    private static final int RC_HANDLE_GMS = 9001;  //  카메라 권한 변수
    private static final int RC_HANDLE_CAMERA_PERM = 2; //  카메라 권한 변수

    private FrameLayout QRCodeLayout;

    private ZXingScannerView QRcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_scanner);

        QRCodeLayout = findViewById(R.id.QRCodeLayout);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(rc == PackageManager.PERMISSION_GRANTED) {
            QRcodeScanStart();
        }else {
            requestCameraPermission();
        }
    }

    public void QRcodeScanStart() {
        QRcodeScannerView = new ZXingScannerView(this);
        setContentView(QRcodeScannerView);
        QRcodeScannerView.setResultHandler(this);
        QRcodeScannerView.startCamera();
    }

    /*
     *
     *   카메라 권한 관련 메소드
     *
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(QRCodeLayout, R.string.CameraPermissionScript, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.PermissionOK, listener)
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            QRcodeScanStart();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert)
                .setMessage(R.string.CameraPermissionScript)
                .setPositiveButton(R.string.PermissionOK, listener)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        QRcodeScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("QRcodeResultAddr", result.getText());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}

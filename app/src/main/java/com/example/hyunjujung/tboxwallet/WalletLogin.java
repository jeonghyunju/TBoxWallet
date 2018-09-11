package com.example.hyunjujung.tboxwallet;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyunjujung.tboxwallet.database.WalletDBHelper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 *  [ 어플 실행시 처음 나타나는 대기 화면 ]
 *
 *  - 데이터베이스에 지갑이 생성 되어있을 경우 계정 잔액 화면으로 넘어가고
 *  - 지갑이 생성되어 있지 않다면 create 화면으로 넘어간다
 *
 */

public class WalletLogin extends AppCompatActivity {
    private static final String TAG = "WalletLogin";

    /* 지갑 아이디, 비밀번호, 경로 저장할 데이터베이스 */
    WalletDBHelper walletDB;

    JSONObject walletJSON = new JSONObject();    //  계정이 존재하면 해당 계정의 모든 정보를 담을 jsonObejct
    ArrayList<JSONObject> walletID_array = new ArrayList<>();   // 데이터베이스에 존재하는 모든 지갑 계정을 담을 어레이리스트

    EditText loginID_et;

    String filePath = "";

    SharedPreferences loginShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_login);

        loginID_et = findViewById(R.id.loginID_et);

        checkPermission();  //  External Storage 권한 체크

        walletDB = new WalletDBHelper(this, "WalletPath.db", null, 1);  // DB 생성
        walletID_array = walletDB.selectAll();  // 데이터베이스에 저장된 지갑 아이디를 모두 가져온다
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        /* Shared 에 아이디 저장되어 있으면 자동로그인 */
        loginShared = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        if(loginShared.getString("AutoLoginID", null) != null) {
            walletJSON = walletDB.selectWallet(loginShared.getString("AutoLoginID", null));
            Intent goAutoMain = new Intent(this, WalletMain.class);
            try {
                goAutoMain.putExtra("WalletID", walletJSON.getString("WalletID"));
                goAutoMain.putExtra("WalletPW", walletJSON.getString("WalletPW"));
                goAutoMain.putExtra("WalletPath", filePath + "/" + walletJSON.getString("WalletPath"));
                startActivity(goAutoMain);
                finish();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void goLogin(View view) {
        switch (view.getId()) {
            case R.id.LoginBtn:
                try {
                    /* 빈 칸 예외처리 */
                    if(loginID_et.getText() == null || loginID_et.getText().toString().equals("")) {
                        Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();

                    /* 데이터베이스에 계정이 존재하지 않는경우 지갑 생성 화면으로 넘어간다 */
                    }else if(loginID_et.getText() != null && !walletID_array.toString().contains(loginID_et.getText().toString())){
                        Toast.makeText(this, "계정이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                        Intent goCreate = new Intent(this, CreateWallet.class);
                        startActivity(goCreate);
                        finish();

                    /* 데이터베이스에 계정이 존재하면 지갑 아이디, 비밀번호, 경로를 가지고 메인 화면으로 넘어간다 */
                    }else {
                        walletJSON = walletDB.selectWallet(loginID_et.getText().toString());
                        /* 한번 로그인 하면 Shared 에 아이디 저장 */
                        SharedPreferences autoLoginShared = getSharedPreferences("AutoLogin", MODE_PRIVATE);
                        SharedPreferences.Editor autoLoginEdit = autoLoginShared.edit();
                        autoLoginEdit.putString("AutoLoginID", loginID_et.getText().toString());
                        autoLoginEdit.apply();

                        /* 메인 화면으로 넘어감 */
                        Intent goMain = new Intent(this, WalletMain.class);
                        goMain.putExtra("WalletID", walletJSON.getString("WalletID"));
                        goMain.putExtra("WalletPW", walletJSON.getString("WalletPW"));
                        goMain.putExtra("WalletPath", filePath + "/" + walletJSON.getString("WalletPath"));
                        startActivity(goMain);
                        finish();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    /* 권한 */
    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Read External Storage Permission is Granted");
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
        }else {
            Log.v(TAG, "Read External Storage Permission is Granted");
        }

        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Write External Storage Permission is Granted");
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }else {
            Log.v(TAG, "Write External Storage Permission is Granted");
        }
    }
}

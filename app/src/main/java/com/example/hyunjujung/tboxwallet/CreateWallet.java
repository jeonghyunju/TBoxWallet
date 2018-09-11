package com.example.hyunjujung.tboxwallet;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyunjujung.tboxwallet.database.WalletDBHelper;

import org.web3j.crypto.WalletUtils;

import java.io.File;

/**
 *  [ 지갑 계정 생성하는 화면 ]
 *
 *  - 지갑의 아이디(닉네임) 와 비밀번호를 입력받아 지갑 계정을 생성한다
 *  - 지갑이 생성되면 지갑 아이디, 비밀번호, 지갑 경로를 SQlite 에 저장한다
 *  - SQlite 에 저장한 후 WalletMain 으로 넘어감
 */

public class CreateWallet extends AppCompatActivity {
    private static final String TAG = "CreateWallet";

    EditText WalletID_et, WalletPW_et;

    /* 지갑 아이디, 비밀번호, 경로 저장할 데이터베이스 */
    WalletDBHelper walletDB;

    private String walletFileName = "";     //  지갑의 계정 파일이 생성된 경로를 저장하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_wallet);

        walletDB = new WalletDBHelper(this, "WalletPath.db", null, 1);  // DB 생성

        WalletID_et = findViewById(R.id.WalletID_et);
        WalletPW_et = findViewById(R.id.WalletPW_et);
    }

    public void createAccount(View view) {
        switch (view.getId()) {
            case R.id.createDoneBtn:
                try {
                    if(WalletID_et.getText().toString().equals("") ||
                            WalletID_et.getText() == null ||
                            WalletPW_et.getText().toString().equals("") ||
                            WalletPW_et.getText()== null) {
                        Toast.makeText(this, "아이디나 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }else {
                        walletFileName = createMobileWallet(WalletPW_et.getText().toString());  // 지갑 계정 생성
                        Intent goMain = new Intent(this, WalletMain.class);
                        goMain.putExtra("WalletID", WalletID_et.getText().toString());
                        goMain.putExtra("WalletPW", WalletPW_et.getText().toString());
                        goMain.putExtra("WalletPath", walletFileName);
                        startActivity(goMain);
                        finish();
                    }
                    Log.e(TAG, walletFileName);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /* 지갑 계정의 정보가 담긴 파일을 기기 내부의 저장소에 저장한다 (계정 생성) */
    public String createMobileWallet(String walletPass) throws Exception {
        String walletPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String fileName = WalletUtils.generateLightNewWalletFile(walletPass, new File(walletPath));
        saveDataBase(WalletID_et.getText().toString(), WalletPW_et.getText().toString(), fileName);
        return walletPath + "/" + fileName;
    }

    /* 계정이 생성되면 데이터베이스에 지갑 아이디, 비밀번호, 지갑 경로를 저장한다 */
    public void saveDataBase(String walletId, String walletpw, String walletpath) {
        walletDB.insertWallet(walletId, walletpw, walletpath);
    }

    /* 입력된 아이디와  */
}

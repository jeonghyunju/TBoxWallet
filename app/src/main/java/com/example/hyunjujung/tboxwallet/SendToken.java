package com.example.hyunjujung.tboxwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.tboxwallet.contract.TboxTestToken;
import com.example.hyunjujung.tboxwallet.database.WalletDBHelper;
import com.example.hyunjujung.tboxwallet.dialog.TransactionDialog;
import com.example.hyunjujung.tboxwallet.qrcode.QRcodeScanner;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;

/**
 *
 *  [ 다른 사용자에게 토큰 보내는 화면 ]
 *
 */

public class SendToken extends AppCompatActivity{
    private static final String TAG = "SendToken";
    private static final String NODE = "https://ropsten.infura.io/v3/843e504e0d67479cb6342eafe1aa9bf6"; // Ropsten 가상 노드
    private static final String CONTRACT = "0xf4038072411A7301316bE48eBb4424A102855aBb";    // Contract 주소
    private static final int QR_RESULT = 100;

    LinearLayout sendTokenLayout;
    TextView sendTokenFrom;
    EditText sendAmountEt, sendTokenTo;

    Intent addressIntent;

    String walletAddress;
    String toAddress;

    WalletDBHelper walletDB;    // 지갑 아이디, 비밀번호, 경로 저장된 데이터베이스

    JSONObject walletJson = new JSONObject();   //  지갑 아이디, 비밀번호, 경로 저장할 jsonObject

    BigInteger GasPrice = ManagedTransaction.GAS_PRICE.multiply(BigInteger.valueOf(2));  // 트랜잭션 수행 시에 소비되는 가스 가격
    BigInteger GasLimit = Contract.GAS_LIMIT.multiply(BigInteger.valueOf(1));   // 최소 가스

    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    TransactionDialog transactionDialog;    // 프로그레스 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_token);

        sendTokenLayout = findViewById(R.id.sendTokenLayout);
        sendTokenFrom = findViewById(R.id.sendTokenFrom);
        sendTokenTo = findViewById(R.id.sendTokenTo);
        sendAmountEt = findViewById(R.id.sendAmountEt);

        /* WalletMain 에서 넘어온 현재 사용자 지갑 주소 */
        addressIntent = getIntent();
        walletAddress = addressIntent.getStringExtra("WalletAddress");

        walletDB = new WalletDBHelper(this, "WalletPath.db", null, 1);  // DB 생성
        walletJson = walletDB.selectWallet(addressIntent.getStringExtra("WalletID"));   //  데이터베이스에서 지갑 아이디, 비밀번호, 경로 가져옴

        sendTokenFrom.setText(addressIntent.getStringExtra("WalletID"));    // 보내는 사람 아이디
        //sendTokenTo.setText(TO_ADDRESS);  // 받는 사람 지갑 주소

        Log.e(TAG, "내 토큰 잔액 : " + (addressIntent.getIntExtra("WalletBalance", 11))/1000 + ", 가스 : " + GasPrice);

    }

    public void sendTokenOnclick(View view) {
        switch (view.getId()) {
            /* 현재 화면 닫기 */
            case R.id.closeBtn:
                finish();
                break;

            /* 수량 -1 */
            case R.id.sendAmountMinusBtn:
                if(sendAmountEt.getText() == null || sendAmountEt.getText().toString().equals("") || Integer.parseInt(sendAmountEt.getText().toString()) <= 0) {
                    sendAmountEt.setText("0");
                }
                sendAmountEt.setText(Integer.parseInt(sendAmountEt.getText().toString()) - 1 + "");
                sendAmountEt.setSelection(sendAmountEt.length());
                break;

            /* 수량 +1 */
            case R.id.sendAmountPlusBtn:
                if(sendAmountEt.getText() == null || sendAmountEt.getText().toString().equals("")) {
                    sendAmountEt.setText("0");
                }
                sendAmountEt.setText(Integer.parseInt(sendAmountEt.getText().toString()) + 1 + "");
                sendAmountEt.setSelection(sendAmountEt.length());
                break;

            /* 토큰 보내기
             *  - 보낼 토큰 수량은 0개 이상
             *  - 내가 가진 토큰보다 보낼 토큰 수량이 더 많으면 안됨
             *  - 받는 사람의 주소가 입력되어 있어야함 */
            case R.id.SendTokenDoneBtn:
                if(Integer.parseInt(sendAmountEt.getText().toString()) <= 0 || sendAmountEt.getText() == null || sendAmountEt.getText().toString().equals("")) {
                    Toast.makeText(this, "수량은 0개 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(sendAmountEt.getText().toString()) > addressIntent.getIntExtra("WalletBalance", 11)/1000){
                    Toast.makeText(this, "토큰 잔액이 부족합니다", Toast.LENGTH_SHORT).show();
                }else if(sendTokenTo.getText().toString().equals("") || sendTokenTo.getText() == null){
                    Toast.makeText(this, "받는 사람의 주소를 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    new SendTokenAsync().execute();
                }
                break;

            /* 받는 사람 지갑 주소 QR code 스캔하기 */
            case R.id.qrCodeScanBtn:
                Intent goQRActivity = new Intent(this, QRcodeScanner.class);
                startActivityForResult(goQRActivity, QR_RESULT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == QR_RESULT) {
            if(resultCode == Activity.RESULT_OK) {
                sendTokenTo.setText(data.getStringExtra("QRcodeResultAddr").substring(9));
                sendTokenTo.setSelection(sendTokenTo.length());
                toAddress = data.getStringExtra("QRcodeResultAddr").substring(9);
            }
        }
    }

    /*
    *
    *   토큰 보내기 트랜잭션 수행하는 AsyncTask
    *
    *
    * */
    class SendTokenAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Web3j web3j = Web3jFactory.build(new HttpService(NODE));
            String blockNum = "";
            transactionDialog = new TransactionDialog();    // 프로그레스 다이얼로그 띄움
            transactionDialog.show(getSupportFragmentManager(), "Sending...");
            try {

                Credentials tboxCredential = WalletUtils.loadCredentials(   //  filePath 경로에 있는 지갑 불러옴
                                                    walletJson.getString("WalletPW"),
                                                    filePath + "/" + walletJson.getString("WalletPath"));
                Log.i(TAG,"Credential Loaded" + walletJson.getString("WalletPath"));

                TboxTestToken tboxContract = TboxTestToken.load(CONTRACT, web3j, tboxCredential, GasPrice, GasLimit);   //  Contract 불러옴

                TransactionReceipt sendTransaction = tboxContract.transfer(     //  TO_ADDRESS 주소로 토큰 보내기
                                                                    toAddress,
                                                                    BigInteger.valueOf(Long.parseLong(sendAmountEt.getText().toString()) * 1000)).send();

                Log.e(TAG, sendTransaction.getBlockNumber() + "");

            }catch (Exception e) {
                e.printStackTrace();
            }
            return blockNum;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            transactionDialog.dismiss();
            finish();
        }
    }
}

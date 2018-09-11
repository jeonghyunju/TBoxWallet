package com.example.hyunjujung.tboxwallet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyunjujung.tboxwallet.contract.TboxTestToken;
import com.example.hyunjujung.tboxwallet.database.WalletDBHelper;
import com.example.hyunjujung.tboxwallet.dialog.TransactionDialog;

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
 *  [ 토근 구입하는 화면 ]
 *
 */

public class BuyToken extends AppCompatActivity {
    private static final String TAG = "BuyToken";
    private static final String NODE = "https://ropsten.infura.io/v3/843e504e0d67479cb6342eafe1aa9bf6";     // Ropsten 가상 노드
    private static final String CONTRACT = "0xf4038072411A7301316bE48eBb4424A102855aBb";    // Contract 주소
    private static final String OWNER_ADDRESS = "0x2f09476063ab1c26a3a2974fa18819bb1913af38";   // 토큰 Owner 계정 주소

    EditText amountEt;

    String walletAddress;
    Intent addressIntent;

    WalletDBHelper walletDB;    // 지갑 아이디, 비밀번호, 경로 저장된 데이터베이스

    JSONObject walletJson = new JSONObject();   //  지갑 아이디, 비밀번호, 경로 저장할 jsonObject

    BigInteger GasPrice = ManagedTransaction.GAS_PRICE.multiply(BigInteger.valueOf(4));  // 트랜잭션 수행 시에 소비되는 가스 가격
    BigInteger GasLimit = Contract.GAS_LIMIT.multiply(BigInteger.valueOf(2));   // 최소 가스

    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    TransactionDialog transactionDialog;    // 프로그레스 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_token);

        amountEt = findViewById(R.id.amountEt);

        addressIntent = getIntent();
        walletDB = new WalletDBHelper(this, "WalletPath.db", null, 1);  // DB 생성
        walletJson = walletDB.selectWallet(addressIntent.getStringExtra("WalletID"));
        walletAddress = addressIntent.getStringExtra("WalletAddress");

    }

    public void buyTokenOnclick(View view) {
        switch (view.getId()) {
            /* 현재 화면 닫기 */
            case R.id.closeBtn:
                finish();
                break;

            /* 현재 EditText 에 입력된 구매수량 -1 */
            case R.id.AmountMinusBtn:
                if(amountEt.getText() == null || amountEt.getText().toString().equals("") || Integer.parseInt(amountEt.getText().toString()) <= 0) {
                    amountEt.setText("0");
                }
                amountEt.setText(Integer.parseInt(amountEt.getText().toString()) - 1 + "");
                amountEt.setSelection(amountEt.length());
                break;

            /* 현재 EditText 에 입력된 구매수량 +1 */
            case R.id.AmountPlusBtn:
                if(amountEt.getText() == null || amountEt.getText().toString().equals("")) {
                    amountEt.setText("0");
                }
                amountEt.setText(Integer.parseInt(amountEt.getText().toString()) + 1 + "");
                amountEt.setSelection(amountEt.length());
                break;

            /* 구매 완료 버튼 */
            case R.id.BuyTokenDoneBtn:
                if(Integer.parseInt(amountEt.getText().toString()) <= 0 || amountEt.getText() == null || amountEt.getText().toString().equals("")) {
                    Toast.makeText(this, "수량은 0개 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                }else {
                    new BuyTokenAsync().execute();
                }
                break;
        }
    }

    /*
    *
    *   토큰 Owner 계정에서 토큰 구매
    *
    *
    * */
    class BuyTokenAsync extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Web3j web3j = Web3jFactory.build(new HttpService(NODE));
            String blockNum = "";
            transactionDialog = new TransactionDialog();    // 프로그레스 다이얼로그 띄움
            transactionDialog.show(getSupportFragmentManager(), "Sending...");
            try{
                Credentials tboxCredential = WalletUtils.loadCredentials(
                                            walletJson.getString("WalletPW"),
                                            filePath + "/" + walletJson.getString("WalletPath"));
                Log.i(TAG,"Credential Loaded" + walletJson.getString("WalletPath"));

                TboxTestToken tboxContract = TboxTestToken.load(CONTRACT, web3j, tboxCredential, GasPrice, GasLimit);   //  Contract 불러옴

                TransactionReceipt buyTransaction = tboxContract.transferFrom(  // 토큰 owner 계정에서 토큰 구매
                                                                OWNER_ADDRESS,  // 토큰 owner 지갑 주소
                                                                walletAddress,  // 구매자 지갑 주소
                                                                BigInteger.valueOf(Long.parseLong(amountEt.getText().toString()) * 1000)).send();

                Log.e(TAG, buyTransaction.getBlockNumber() + "");
                blockNum = buyTransaction.getBlockNumber() + "";
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

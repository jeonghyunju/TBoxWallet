package com.example.hyunjujung.tboxwallet;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hyunjujung.tboxwallet.adapter.TransactionAdapter;
import com.example.hyunjujung.tboxwallet.adapter.TxTapAdapter;
import com.example.hyunjujung.tboxwallet.contract.TboxTestToken;
import com.example.hyunjujung.tboxwallet.data.TransactionData;
import com.example.hyunjujung.tboxwallet.fragment.TxAllFrag;
import com.example.hyunjujung.tboxwallet.fragment.TxEthFrag;
import com.example.hyunjujung.tboxwallet.fragment.TxTokenFrag;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;

/**
 *
 *  [ 지갑 계정 잔액 확인 및 토큰 전송 하는 화면 ]
 *
 *  - 현재 지갑 계정의 잔액을 확인할 수 있다
 *  - 다른 계정으로 토큰을 보낼 수 있다
 */

public class WalletMain extends AppCompatActivity {
    private static final String TAG = "WalletMain";
    private static final String NODE = "https://ropsten.infura.io/v3/843e504e0d67479cb6342eafe1aa9bf6";
    private static final String CONTRACT = "0xf4038072411A7301316bE48eBb4424A102855aBb";
    private static final String TX_URL = "http://api-ropsten.etherscan.io/api?module=account&action=tokentx&address=";
    private static final String TX_BLOCK = "&startblock=0&endblock=999999999&sort=asc&apikey=";
    private static final String ETHERSCAN_API = "GI7JNE95M2XA2M38G1U8HBYVMTNVTTYUSP";

    TextView WalletAccountTv, AccountBalanceTv, EthBalanceTv;
    TabLayout txTabLayout;
    ViewPager txViewPager;
    TxTapAdapter txTapAdapter;
    Intent walletIntent;

    String walletPass, walletPath, walletAddress = "";  // 지갑 계정의 비밀번호, 경로, 주소 저장할 변수
    BigInteger ethBalance;  // 이더리움 잔액 저장할 변수
    BigInteger accountResult = BigInteger.ONE;  //  토큰 잔액 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_main);

        WalletAccountTv = findViewById(R.id.WalletAccountTv);
        AccountBalanceTv = findViewById(R.id.AccountBalanceTv);
        EthBalanceTv = findViewById(R.id.EthBalanceTv);
        txTabLayout = findViewById(R.id.txTabLayout);
        txViewPager = findViewById(R.id.txViewPager);

        walletIntent = getIntent();
        WalletAccountTv.setText(walletIntent.getStringExtra("WalletID"));
        walletPass = walletIntent.getStringExtra("WalletPW");
        walletPath = walletIntent.getStringExtra("WalletPath");

        txTapAdapter = new TxTapAdapter(getSupportFragmentManager());
        txTapAdapter.addFragment(new TxAllFrag(), "All");
        txTapAdapter.addFragment(new TxTokenFrag(), "TBC");
        txTapAdapter.addFragment(new TxEthFrag(), "ETH");

        txViewPager.setAdapter(txTapAdapter);
        txTabLayout.setupWithViewPager(txViewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Web3jConnect().execute(walletPath);
    }

    public void walletMainOnclick(View view) {
        switch (view.getId()) {
            /* 토큰 구매 화면으로 이동 */
            case R.id.BuyTokenBtn:
                Intent goBuyToken = new Intent(this, BuyToken.class);
                goBuyToken.putExtra("WalletID", walletIntent.getStringExtra("WalletID"));
                goBuyToken.putExtra("WalletAddress", walletAddress);
                startActivity(goBuyToken);
                break;

            /* 토큰 보내기 화면으로 이동 */
            case R.id.SendTokenBtn:
                Intent goSendToken = new Intent(this, SendToken.class);
                goSendToken.putExtra("WalletID", walletIntent.getStringExtra("WalletID"));
                goSendToken.putExtra("WalletAddress", walletAddress);
                goSendToken.putExtra("WalletBalance", accountResult.intValue());
                startActivity(goSendToken);
                Log.e(TAG, "지갑 주소 : " + walletAddress);
                break;
        }
    }

    /* 계정에서 토큰 잔액과 이더리움 잔액 가져오는 Async */
    class Web3jConnect extends AsyncTask<String, Void, BigInteger> {

        @Override
        protected BigInteger doInBackground(String... strings) {
            String path = strings[0];
            Web3j web3j = Web3jFactory.build(new HttpService(NODE));

            try {
                Credentials tboxCredential = WalletUtils.loadCredentials(walletPass, path);
                walletAddress = tboxCredential.getAddress(); // 다른 사용자에게 토큰 보내기 위해서 지갑 주소 저장한다

                Log.i(TAG, "Credential loaded" + walletPath + ", Address : " + walletAddress);

                TboxTestToken tboxContract = TboxTestToken.load(CONTRACT, web3j, tboxCredential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                accountResult = tboxContract.balanceOf(tboxCredential.getAddress()).send(); // 지갑 내 토큰 잔액
                EthGetBalance ethGetBalance = web3j.ethGetBalance(tboxCredential.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get(); // 지갑 내 이더리움 잔액
                ethBalance = ethGetBalance.getBalance();

                Log.e(TAG, "지갑 토큰 잔액 : " + accountResult.toString() + ", 이더리움 잔액 : " + ethBalance);

            }catch (Exception e) {
                e.printStackTrace();
            }

            return accountResult;
        }

        @Override
        protected void onPostExecute(BigInteger bigInteger) {
            super.onPostExecute(bigInteger);
            AccountBalanceTv.setText(bigInteger.divide(BigInteger.valueOf(1000)) + ".00");
            EthBalanceTv.setText(Convert.fromWei(ethBalance.toString(), Convert.Unit.ETHER).setScale(4, BigDecimal.ROUND_HALF_UP) + "");
        }
    }

}

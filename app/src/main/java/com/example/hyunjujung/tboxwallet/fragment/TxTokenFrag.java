package com.example.hyunjujung.tboxwallet.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.tboxwallet.R;
import com.example.hyunjujung.tboxwallet.TransactionDetail;
import com.example.hyunjujung.tboxwallet.adapter.TransactionAdapter;
import com.example.hyunjujung.tboxwallet.data.TransactionData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TxTokenFrag extends Fragment {
    private static final String TAG = "TxTokenFrag";
    private static final String TX_URL = "http://api-ropsten.etherscan.io/api?module=account&action=tokentx&address=";
    private static final String TX_BLOCK = "&startblock=0&endblock=999999999&sort=asc&apikey=";
    private static final String ETHERSCAN_API = "GI7JNE95M2XA2M38G1U8HBYVMTNVTTYUSP";

    RecyclerView TxTokenRecycle;
    LinearLayoutManager TxTokenLM;
    TransactionAdapter txAdapter;
    ArrayList<TransactionData> txTokenArray = new ArrayList<>();

    TextView noTxTokenList;

    String walletAddress;

    private GestureDetector gestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.txtoken_frag, container, false);
        TxTokenRecycle = view.findViewById(R.id.TxTokenRecycle);
        noTxTokenList = view.findViewById(R.id.noTxTokenList);
        TxTokenLM = new LinearLayoutManager(getContext());
        TxTokenRecycle.setLayoutManager(TxTokenLM);

        gestureDetector = new GestureDetector(getActivity().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        /* 지갑 주소를 얻기 위해서 WalletLogin 에서 넘어온 WalletPath 를 사용한다
         *  - WalletPath 에서 파일명만 알아낸다
         *  - 파일명에서 확장자를 자른다
         *  - 앞에 0x 를 붙여주면 지갑 주소 완성 */
        Intent addressIntent = getActivity().getIntent();
        int walletIndex = (addressIntent.getStringExtra("WalletPath").lastIndexOf("/"));
        String tempAddress = (addressIntent.getStringExtra("WalletPath")).substring(walletIndex + 1);
        int secondIndex = tempAddress.lastIndexOf("-");
        int thirdIndex = tempAddress.lastIndexOf(".");
        walletAddress = "0x" + tempAddress.substring(secondIndex + 1, thirdIndex);

        /* 리사이클러뷰 아이템터치리스너 */
        TxTokenRecycle.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && gestureDetector.onTouchEvent(e)) {
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    TransactionData currentTD = txTokenArray.get(currentPosition);
                    //Toast.makeText(getActivity().getApplicationContext(), currentTD.getFrom(), Toast.LENGTH_SHORT).show();

                    Intent goDetail = new Intent(getActivity().getApplicationContext(), TransactionDetail.class);
                    goDetail.putExtra("TransactionDetail", currentTD);
                    goDetail.putExtra("walletAddress", walletAddress);
                    startActivity(goDetail);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new getTokenTransaction().execute(walletAddress);
    }

    /* 계정의 모든 토큰 트랜잭션 가져오는 Async (EtherScan Api 사용) */
    class getTokenTransaction extends AsyncTask<String, Void, ArrayList<TransactionData>> {

        @Override
        protected ArrayList<TransactionData> doInBackground(String... strings) {
            String address = strings[0];
            String url = TX_URL + address + TX_BLOCK + ETHERSCAN_API;
            Log.e(TAG, "Transaction URL : " + url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonElement rootObject = jsonParser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");

                TransactionData[] transactionData = gson.fromJson(rootObject, TransactionData[].class);
                if(txTokenArray.size() > 0) {
                    txTokenArray.clear();
                }
                if(transactionData.length > 0) {
                    for(TransactionData td : transactionData) {
                        txTokenArray.add(td);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return txTokenArray;
        }

        @Override
        protected void onPostExecute(ArrayList<TransactionData> transactionData) {
            super.onPostExecute(transactionData);
            Log.e(TAG, transactionData.toString());
            if(txTokenArray.size() > 0) {
                txAdapter = new TransactionAdapter(getContext(), txTokenArray, walletAddress);
                TxTokenRecycle.setAdapter(txAdapter);
            }else {
                noTxTokenList.setVisibility(View.VISIBLE);
            }
        }
    }
}

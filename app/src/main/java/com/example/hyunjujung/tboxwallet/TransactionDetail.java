package com.example.hyunjujung.tboxwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyunjujung.tboxwallet.data.TransactionData;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TransactionDetail extends AppCompatActivity {
    private static final String TAG = "TransactionDetail";
    TransactionData transactionDetail;
    String walletAddress;

    ImageView txImage;
    TextView txAmount, txTitle, txBlockNum, txFrom, txTo, txContract, txGas, txGasPrice, txDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_detail);
        txImage = findViewById(R.id.txImage);
        txAmount = findViewById(R.id.txAmount);
        txTitle = findViewById(R.id.txTitle);
        txBlockNum = findViewById(R.id.txBlockNum);
        txFrom = findViewById(R.id.txFrom);
        txTo = findViewById(R.id.txTo);
        txContract = findViewById(R.id.txContract);
        txGas = findViewById(R.id.txGas);
        txGasPrice = findViewById(R.id.txGasPrice);
        txDate = findViewById(R.id.txDate);

        transactionDetail = (TransactionData)getIntent().getSerializableExtra("TransactionDetail");
        walletAddress = getIntent().getStringExtra("walletAddress");

        txDate.setText(new SimpleDateFormat("YY년 MM월 dd일")
                .format(new Timestamp(Long.parseLong(transactionDetail.getTimeStamp())*1000)));

        if(walletAddress.equals(transactionDetail.getFrom())) {
            txImage.setImageResource(R.drawable.ic_send);
            txAmount.setText("- " + Integer.parseInt(transactionDetail.getValue())/1000 + ".00");
            txAmount.setTextColor(getResources().getColor(R.color.walletTwellveColor));
            txTitle.setText(transactionDetail.getTokenSymbol());
        }else {
            if(transactionDetail.getValue().length() > 6) {
                txImage.setImageResource(R.drawable.ic_ethereum);
                txAmount.setText("+ " + Convert.fromWei(transactionDetail.getValue(), Convert.Unit.ETHER)
                        .setScale(4, BigDecimal.ROUND_HALF_UP));
                txTitle.setText("ETH");
            }else {
                txImage.setImageResource(R.drawable.ic_dollar);
                txAmount.setText("+ " + Integer.parseInt(transactionDetail.getValue())/1000 + ".00");
                txTitle.setText(transactionDetail.getTokenSymbol());
            }
            txAmount.setTextColor(getResources().getColor(R.color.walletThirteenColor));
        }

        txBlockNum.setText(transactionDetail.getBlockNumber());
        txFrom.setText(transactionDetail.getFrom());
        txTo.setText(transactionDetail.getTo());
        txContract.setText(transactionDetail.getContractAddress());
        txGas.setText(transactionDetail.getGas());
        txGasPrice.setText(transactionDetail.getGasPrice());
    }

    public void txDetailOnclick(View view) {
        switch (view.getId()) {
            case R.id.closeActivity:
                finish();
                break;
        }
    }
}

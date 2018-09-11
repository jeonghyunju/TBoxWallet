package com.example.hyunjujung.tboxwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyunjujung.tboxwallet.R;
import com.example.hyunjujung.tboxwallet.data.TransactionData;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{
    Context context;
    ArrayList<TransactionData> txArray;
    String walletAddress;

    public TransactionAdapter(Context context, ArrayList<TransactionData> txArray, String walletAddress) {
        this.context = context;
        this.txArray = txArray;
        this.walletAddress = walletAddress;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(walletAddress.equals(txArray.get(position).getFrom())) {
            //  사용자가 토큰 보냈을때
            holder.txIcon.setImageResource(R.drawable.ic_send);
            holder.txAmount.setText("- " + Integer.parseInt(txArray.get(position).getValue())/1000 + ".00");
            holder.txAmount.setTextColor(context.getResources().getColor(R.color.walletTwellveColor));
            holder.txTitle.setText("Send TBC Token");
            holder.txToken.setText("TBC");
        }else {
            //  사용자가 토큰 받았을때
            if(txArray.get(position).getValue().length() > 6) {
                holder.txIcon.setImageResource(R.drawable.ic_ethereum);
                holder.txAmount.setText("+ " + Convert.fromWei(txArray.get(position).getValue(), Convert.Unit.ETHER)
                        .setScale(4, BigDecimal.ROUND_HALF_UP));
                holder.txTitle.setText("Receive ETH");
                holder.txToken.setText("ETH");
            }else {
                holder.txIcon.setImageResource(R.drawable.ic_dollar);
                holder.txAmount.setText("+ " + Integer.parseInt(txArray.get(position).getValue())/1000 + ".00");
                holder.txTitle.setText("Receive TBC Token");
                holder.txToken.setText("TBC");
            }
            holder.txAmount.setTextColor(context.getResources().getColor(R.color.walletThirteenColor));
        }
        holder.txTime.setText(new SimpleDateFormat("YY년 MM월 dd일")
                .format(new Timestamp(Long.parseLong(txArray.get(position).getTimeStamp())*1000)));
    }

    @Override
    public int getItemCount() {
        return txArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView txIcon;
        public TextView txTitle, txTime, txAmount, txToken;
        public ViewHolder(View itemView) {
            super(itemView);
            txIcon = (ImageView)itemView.findViewById(R.id.txIcon);
            txTitle = (TextView)itemView.findViewById(R.id.txTitle);
            txTime = (TextView)itemView.findViewById(R.id.txTime);
            txAmount = (TextView)itemView.findViewById(R.id.txAmount);
            txToken = (TextView)itemView.findViewById(R.id.txToken);
        }
    }
}

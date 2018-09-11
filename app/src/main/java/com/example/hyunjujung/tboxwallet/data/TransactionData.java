package com.example.hyunjujung.tboxwallet.data;

import java.io.Serializable;

public class TransactionData implements Serializable{
    private String blockNumber;
    private String timeStamp;
    private String hash;
    private String nonce;
    private String blockhash;
    private String from;
    private String contractAddress;
    private String to;
    private String value;
    private String tokenName;
    private String tokenSymbol;
    private String tokenDecimal;
    private String transactionIndex;
    private String gas;
    private String gasPrice;
    private String gasUsed;
    private String cumulativeGasUsed;
    private String input;
    private String confirmations;

    public TransactionData() {
    }

    public TransactionData(String blockNumber, String timeStamp, String hash, String nonce, String blockhash, String from, String contractAddress, String to, String value, String tokenName, String tokenSymbol, String tokenDecimal, String transactionIndex, String gas, String gasPrice, String gasUsed, String cumulativeGasUsed, String input, String confirmations) {
        this.blockNumber = blockNumber;
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.nonce = nonce;
        this.blockhash = blockhash;
        this.from = from;
        this.contractAddress = contractAddress;
        this.to = to;
        this.value = value;
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.tokenDecimal = tokenDecimal;
        this.transactionIndex = transactionIndex;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.gasUsed = gasUsed;
        this.cumulativeGasUsed = cumulativeGasUsed;
        this.input = input;
        this.confirmations = confirmations;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }
}

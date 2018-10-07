package com.cscjoke.bitcoinj.bean;

import com.cscjoke.bitcoinj.utils.RSAUtils;

import java.security.PublicKey;

public class Transaction {
    //付款方公钥
    public String senderPublickKey;

    //收款方公钥
    public String receiverPublickKey;
    //签名
    public String signature;
    //转账信息
    public String content;

    public Transaction(){}

    public Transaction(String senderPublickKey, String receiverPublickKey, String signature, String content) {
        this.senderPublickKey = senderPublickKey;
        this.receiverPublickKey = receiverPublickKey;
        this.signature = signature;
        this.content = content;
    }

    //校验交易是否有效

    public boolean verify(){

        PublicKey sender = RSAUtils.getPublicKeyFromString("RSA", senderPublickKey);

        return RSAUtils.verifyDataJS("SHA256withRSA", sender, content, signature);
    }

    public String getSenderPublickKey() {
        return senderPublickKey;
    }

    public void setSenderPublickKey(String senderPublickKey) {
        this.senderPublickKey = senderPublickKey;
    }

    public String getReceiverPublickKey() {
        return receiverPublickKey;
    }

    public void setReceiverPublickKey(String receiverPublickKey) {
        this.receiverPublickKey = receiverPublickKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "senderPublickKey='" + senderPublickKey + '\'' +
                ", receiverPublickKey='" + receiverPublickKey + '\'' +
                ", signature='" + signature + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

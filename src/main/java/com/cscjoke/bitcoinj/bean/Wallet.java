package com.cscjoke.bitcoinj.bean;

import com.cscjoke.bitcoinj.utils.RSAUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {
    public PublicKey publicKey;
    public PrivateKey privateKey;

    public Wallet(String name){
        File pubFile = new File(name + ".pub");
        File priFile = new File(name + ".pri");

        if (!priFile.exists() || priFile.length() == 0){
            RSAUtils.generateKeys("RSA", name + ".pri", name + ".pub");
            publicKey = RSAUtils.getPublicKeyFromFile("RSA", name + ".pub");
            privateKey = RSAUtils.getPrivateKey("RSA", name + ".pri");
        }
    }

    //转账
    public Transaction sendMoney(String receiverPublickKey, String content){

        String publicKeyEncode = Base64.encode(publicKey.getEncoded());
        //生成签名
        String signature = RSAUtils.getSignature("SHA256withRSA", privateKey, content);
        //生成交易对象

        Transaction transaction = new Transaction(publicKeyEncode, receiverPublickKey, signature, content);
        return transaction;

    }

}

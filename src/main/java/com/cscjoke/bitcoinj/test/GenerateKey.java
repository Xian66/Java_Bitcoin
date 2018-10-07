package com.cscjoke.bitcoinj.test;

import com.cscjoke.bitcoinj.utils.RSAUtils;

public class GenerateKey {
    public static void main(String[] args) {
        RSAUtils.generateKeysJS("RSA","receive.pri","receive.pub");
    }
}

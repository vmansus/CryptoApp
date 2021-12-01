package com.example.myapplication.smcipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.security.Security;


public class SM4KeyHelper {
    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] generateKey() throws Exception{
        KeyGenerator kg = KeyGenerator.getInstance("SM4", BouncyCastleProvider.PROVIDER_NAME);
        kg.init(128, new SecureRandom());
        return kg.generateKey().getEncoded();
    }
}
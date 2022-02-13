package com.example.myapplication.streamcipher;

import java.math.BigInteger;
import java.util.Random;

public class RandomZucKeyGenerater {

    public String makeKey() {
        Random random = new Random();
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) random.nextInt(256);

        String hex = new BigInteger(1, bytes).toString(16);
        return hex;
    }
}

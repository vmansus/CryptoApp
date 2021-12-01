package com.example.myapplication.smcipher;

import com.example.myapplication.ProperTies;
import com.example.myapplication.PropertiesUtils;

import java.util.Properties;

public class TestProperties {
    public static void main(String[] args) {
        String url = PropertiesUtils.getProperties(null).getProperty("node.info.encryptlist[0]");
        System.out.println(url);
    }

}

package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;

import org.bouncycastle.jcajce.provider.keystore.PKCS12;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class PropertiesUtils {

    //1、配置文件的位置在assets资源目录下
    private final static String m_strPath = "appConfig.properties";
    private final static String m_keystorePath = "ccc";
    //2、配置文件的位置在源代码根目录(src下)
    //private final static String m_strPath = "/global.properties";

    public static Properties getProperties(Context c) {
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context获取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            //InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
//            InputStream in = PropertiesUtils.class.getResourceAsStream(m_strPath);
            props.load(c.getAssets().open("appConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }


    public static KeyStore getkeyStore(Context c) throws Exception{
        KeyStore keyStore = KeyStore.getInstance("BKS");
        InputStream is = null;
        try {
            //方法一：通过activity中的context获取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            //InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
//            InputStream in = PropertiesUtils.class.getResourceAsStream(m_strPath);
            is=c.getAssets().open("ccc");
            keyStore.load(is,"12345678".toCharArray());
//            X509Certificate certificate= (X509Certificate) keyStore.getCertificate("server cert");
//            System.out.println(certificate.getIssuerDN());
            is.close();
//            keyStore.load();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            is.close();
        }
        return keyStore;
    }

    /**
     * 使用样例
     */
    private void example() {
        //String url = PropertiesUtils.getProperties(getApplicationContext()).getProperty("url");
        String url = PropertiesUtils.getProperties(null).getProperty("url");
    }


}
 


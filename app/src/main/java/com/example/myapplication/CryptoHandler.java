package com.example.myapplication;

import com.example.myapplication.smcipher.JsonAsWholeDecUtils;
import com.example.myapplication.smcipher.JsonAsWholeEncUtils;
import com.example.myapplication.smcipher.JsonDecryptUtils;
import com.example.myapplication.smcipher.JsonUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class CryptoHandler {

    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();
//    private static final String TEST_P12_FILENAME="C:\\Users\\20781\\AndroidStudioProjects\\MyApplication\\app\\src\\main\\res\\clientkeystore.p12";



    //处理响应消息
    public String encRequest(String responseBody,String encryptCert,String signkey) throws Exception {
        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
        int mode = Integer.parseInt(pro.getProperty("workmode"));
        int isWhole = Integer.parseInt(pro.getProperty("isWhole"));


        //keyname即加密所用证书DN
        String keyname;



        KeyStore ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        X509Certificate cert= (X509Certificate) ks.getCertificate(encryptCert);

        keyname=cert.getSubjectDN().toString();

        if(isWhole==0){
            if(mode==0){
                responseBody= JsonUtils.getInstance(cert).jsonEncrypt(responseBody,keyname,signkey);
            }else if(mode==1){
                responseBody=JsonUtils.getInstance(cert).jsonEncryptmode1(responseBody,keyname,signkey);
            }else if(mode==2){
                responseBody= JsonUtils.getInstance(cert).jsonEncryptmode2(responseBody,keyname,signkey);
            }
        }else if(isWhole==1){
            if(mode==0){
                responseBody= JsonAsWholeEncUtils.getInstance(cert).jsonEncrypt(responseBody,keyname,signkey);
            }else if(mode==1){
                responseBody= JsonAsWholeEncUtils.getInstance(cert).jsonEncrypt1(responseBody,keyname,signkey);
            }else if(mode==2){
                responseBody= JsonAsWholeEncUtils.getInstance(cert).jsonEncrypt2(responseBody,keyname,signkey);
            }
        }


        return responseBody;
    }

    //处理请求消息
    public String decResponse(String requestBody) throws Exception {
        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
        int mode = Integer.parseInt(pro.getProperty("workmode"));
        int isWhole = Integer.parseInt(pro.getProperty("isWhole"));
        if(isWhole==0){
            if(mode==0){
                requestBody= new JsonDecryptUtils().jsonDecrypt(requestBody);
            }else if(mode==1){
                requestBody= new JsonDecryptUtils().jsonDecryptmode1(requestBody);
            }else if(mode==2){
                requestBody= new JsonDecryptUtils().jsonDecryptmode2(requestBody);
            }
        }else if(isWhole==1){
            if(mode==0){
                requestBody= new JsonAsWholeDecUtils().jsonDecrypt(requestBody);
            }else if(mode==1){
                requestBody= new JsonAsWholeDecUtils().jsonDecrypt1(requestBody);
            }else if(mode==2){
                requestBody= new JsonAsWholeDecUtils().jsonDecrypt2(requestBody);
            }
        }


        return requestBody;
    }
}

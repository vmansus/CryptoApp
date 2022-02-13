package com.example.myapplication;

import com.example.myapplication.smcipher.FormDataDec;
import com.example.myapplication.smcipher.FormDataEnc;
import com.example.myapplication.smcipher.JsonAsWholeDecUtils;
import com.example.myapplication.smcipher.JsonAsWholeEncUtils;
import com.example.myapplication.smcipher.JsonDecryptUtils;
import com.example.myapplication.smcipher.JsonUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FormHandler {

    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();
//    private static final String TEST_P12_FILENAME="C:\\Users\\20781\\AndroidStudioProjects\\MyApplication\\app\\src\\main\\res\\clientkeystore.p12";



    //处理响应消息
    public Map encRequest(Map map,String encryptCert,String signkey) throws Exception {
        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
        int mode = Integer.parseInt(pro.getProperty("workmode"));
//        int isWhole = Integer.parseInt(pro.getProperty("isWhole"));


        //keyname即加密所用证书DN
        String keyname;



        KeyStore ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        X509Certificate cert= (X509Certificate) ks.getCertificate(encryptCert);
//        System.out.println(cert);

        keyname=cert.getSubjectDN().toString();
        if(mode==0){
                map= FormDataEnc.getInstance(cert).FormDataEnc(map,keyname,signkey);
        }else if(mode==1){
                map= FormDataEnc.getInstance(cert).FormDataEnc1(map,keyname,signkey);
        }else if(mode==2){
                map= FormDataEnc.getInstance(cert).FormDataEnc2(map,keyname,signkey);
        }


        return map;
    }

    //处理请求消息
    public Map decResponse(Map requestBody) throws Exception {
        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
        int mode = Integer.parseInt(pro.getProperty("workmode"));
//        int isWhole = Integer.parseInt(pro.getProperty("isWhole"));

//        if(isWhole==0){
            if(mode==0){
                requestBody= new FormDataDec().FormDataDec(requestBody);
            }else if(mode==1){
                requestBody= new FormDataDec().FormDataDec1(requestBody);
            }else if(mode==2){
                requestBody= new FormDataDec().FormDataDec2(requestBody);
            }
//        }else if(isWhole==1){
//            if(mode==0){
//                requestBody= new JsonAsWholeDecUtils().jsonDecrypt(requestBody);
//            }else if(mode==1){
//                requestBody= new JsonAsWholeDecUtils().jsonDecrypt1(requestBody);
//            }else if(mode==2){
//                requestBody= new JsonAsWholeDecUtils().jsonDecrypt2(requestBody);
//            }
//        }


        return requestBody;
    }
}

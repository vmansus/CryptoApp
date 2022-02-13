package com.example.myapplication;

import com.example.myapplication.smcipher.FormDataDec;
import com.example.myapplication.smcipher.RawDataDec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Map;
import java.util.Properties;

public class HtmlHandler {


    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();

    //处理请求消息
    public String decHtml(String requestBody) throws Exception {
        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
        int mode = Integer.parseInt(pro.getProperty("htmlmode"));
//        int isWhole = Integer.parseInt(pro.getProperty("isWhole"));

//        if(isWhole==0){
        if(mode==0){
            requestBody= new RawDataDec().decWholeHtml(requestBody);
        }else if(mode==3){
            requestBody= new RawDataDec().decPartHtml(requestBody);
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

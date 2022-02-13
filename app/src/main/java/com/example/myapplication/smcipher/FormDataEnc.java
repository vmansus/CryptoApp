package com.example.myapplication.smcipher;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.myapplication.CryptoConfigUtils;
import com.example.myapplication.MyApplication;
import com.example.myapplication.PropertiesUtils;
import com.example.myapplication.gmhelper.SM2Util;
import com.example.myapplication.gmhelper.SM4Util;
import com.example.myapplication.gmhelper.cert.SM2X509CertMaker;
import com.example.myapplication.streamcipher.BCZuc;
import com.example.myapplication.streamcipher.RandomZucKeyGenerater;


import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FormDataEnc {
    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();

    Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
    int encalg= new CryptoConfigUtils().getEncAlg(pro);
    byte[] sm4key= SM4Util.generateKey();
    String zuckey=new RandomZucKeyGenerater().makeKey();

    private static FormDataEnc instance=null;
    private Key keyEncryptKey=null;


    public FormDataEnc(X509Certificate certificate) throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
        keyEncryptKey=certificate.getPublicKey();
    }

    public  static  FormDataEnc getInstance(X509Certificate certificate) throws Exception{
        if (instance==null)return new FormDataEnc(certificate);
        else return instance;
    }

    NodeMap nodeMap=new NodeMap();
    List<String> formenc=nodeMap.formenc();
    List<String> formsign=nodeMap.formsign();



    public String stringEncrypt(String text) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (encalg==0){
            byte[] iv=Base64.getDecoder().decode(stringToBytes("LvbTKayS1A2NFFBjaPvkJg=="));
            byte[] srcdata=text.getBytes();
            byte[] cipherText= SM4Util.encrypt_CBC_Padding(sm4key,iv,srcdata);
            String ss=bytesToString(Base64.getEncoder().encode(cipherText));
            return ss;
        }else if(encalg==1){
            String res=new BCZuc().stringEncCipher(text,zuckey);
            return res;
        }else {
            System.out.println("请检查对称加密配置!!!");
            return null;
        }
    }

    public String stringSign(String text, String alias) throws Exception {
        KeyStore ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        PrivateKey privateKey=(BCECPrivateKey)ks.getKey(alias,TEST_P12_PASSWD);

        byte[] srcData = text.getBytes();
        Signature sign = Signature.getInstance(SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2, "BC");
        sign.initSign(privateKey);
        sign.update(srcData);
        byte[] signatureValue = sign.sign();

        return bytesToString(Base64.getEncoder().encode(signatureValue));
    }

    public static String Sm2Enc(byte[] srcData, Key publickey) throws InvalidCipherTextException {
        byte[] ciperdata= SM2Util.encrypt((BCECPublicKey) publickey,srcData);
        String ss=bytesToString(Base64.getEncoder().encode(ciperdata));
        return ss;
    }


    public java.security.cert.Certificate[] getCerts(String alias) throws Exception{
        KeyStore  ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        Certificate[] certificates=ks.getCertificateChain(alias);
        return certificates;

    }

    public Map FormDataEnc(Map<String, String> map,String keyname,String alias) throws Exception {
        String encryptkey = null;
        try {
            if (encalg == 0) {
                encryptkey = Sm2Enc(sm4key,keyEncryptKey);
            }else if(encalg==1){
                byte[] zuckeybytes=stringToBytes(zuckey);
                encryptkey=Sm2Enc(zuckeybytes,keyEncryptKey);
            }else {
                System.out.println("请检查对称加密配置!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        JSONObject jsonObject=new JSONObject();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        HashMap<String ,String> sigmap=new HashMap<>();

        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formsign.contains(entry.getKey())){
                sigmap.put(entry.getKey()+"-sig",stringSign(entry.getValue(),alias));
            }
            if(formenc.contains(entry.getKey())){
                entry.setValue(stringEncrypt(entry.getValue()));
            }
        }
        Iterator<Map.Entry<String, String>> sig = sigmap.entrySet().iterator();
        while (sig.hasNext()){
            Map.Entry<String,String> entry=sig.next();
            map.put(entry.getKey(),entry.getValue());
        }
        map.put("Encrypted_Key",encryptkey);
        map.put("KeyName",keyname);
//        String temp=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);
//        String json2=getJsonNew(temp,signedmap);

        for (int i=0;i<getCerts(alias).length;i++){
            map.put("cert"+i,bytesToString(Base64.getEncoder().encode(getCerts(alias)[i].getEncoded())));
        }
        return  map;
    }
    public Map FormDataEnc1(Map<String, String> map,String keyname,String alias) throws Exception {
        String encryptkey = null;
        try {
            if (encalg == 0) {
                encryptkey = Sm2Enc(sm4key,keyEncryptKey);
            }else if(encalg==1){
                byte[] zuckeybytes=stringToBytes(zuckey);
                encryptkey=Sm2Enc(zuckeybytes,keyEncryptKey);
            }else {
                System.out.println("请检查对称加密配置!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formenc.contains(entry.getKey())){
                entry.setValue(stringEncrypt(entry.getValue()));
            }

        }
        map.put("Encrypted_Key",encryptkey);
        map.put("KeyName",keyname);
        return  map;
    }


    public Map FormDataEnc2(Map<String, String> map,String keyname,String alias) throws Exception {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        HashMap<String ,String> sigmap=new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formsign.contains(entry.getKey())){
                sigmap.put(entry.getKey()+"-sig",stringSign(entry.getValue(),alias));
            }
        }
        Iterator<Map.Entry<String, String>> sig = sigmap.entrySet().iterator();
        while (sig.hasNext()){
            Map.Entry<String,String> entry=sig.next();
            map.put(entry.getKey(),entry.getValue());
        }
        for (int i=0;i<getCerts(alias).length;i++){
            map.put("cert"+i,bytesToString(Base64.getEncoder().encode(getCerts(alias)[i].getEncoded())));
        }
        return  map;
    }

    public static byte[] stringToBytes(String str) {
        try {
            // 使用指定的字符集将此字符串编码为byte序列并存到一个byte数组中
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytesToString(byte[] bs) {
        try {
            // 通过指定的字符集解码指定的byte数组并构造一个新的字符串
            return new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

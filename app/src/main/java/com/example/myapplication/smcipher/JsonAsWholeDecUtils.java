package com.example.myapplication.smcipher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.myapplication.MyApplication;
import com.example.myapplication.PropertiesUtils;
import com.example.myapplication.gmhelper.SM2Util;
import com.example.myapplication.gmhelper.SM4Util;
import com.example.myapplication.gmhelper.cert.SM2CertUtil;
import com.example.myapplication.gmhelper.cert.SM2X509CertMaker;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.*;

public class JsonAsWholeDecUtils {
    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();
//    private static final String TEST_P12_FILENAME="C:\\Users\\20781\\AndroidStudioProjects\\MyApplication\\app\\src\\main\\res\\clientkeystore.p12";
    byte[] sm4key = null;

    public static byte[] Sm2Dec(String text, PrivateKey privateKey) throws InvalidCipherTextException {
        byte[] aa= Base64.getDecoder().decode(stringToBytes(text));
        byte[] decryptdata= SM2Util.decrypt((BCECPrivateKey)privateKey,aa);
        return decryptdata;
    }

    public String stringDecrypt(String text) throws BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        byte[] cipherdata=stringToBytes(text);
        byte[] iv=Base64.getDecoder().decode(stringToBytes("LvbTKayS1A2NFFBjaPvkJg=="));
        byte[] decryptedData= SM4Util.decrypt_CBC_Padding(sm4key,iv,Base64.getDecoder().decode(cipherdata));
        return bytesToString(decryptedData);
    }

    public boolean validate(String text, String signaturevalue, X509Certificate cert) throws Exception {
        byte[] temp=Base64.getDecoder().decode(stringToBytes(signaturevalue));
        byte[] srcData=stringToBytes(text);
        Signature verify = Signature.getInstance(SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2, "BC");
        verify.initVerify(cert);
        verify.update(srcData);
        return verify.verify(temp);
    }


    public PrivateKey getPrivateKey(String keyname) throws Exception{
        PrivateKey privateKey = null;
        KeyStore ks=KeyStore.getInstance("PKCS12","BC");
//        try(InputStream is= Files.newInputStream(Paths.get(TEST_P12_FILENAME), StandardOpenOption.READ)){
//            ks.load(is,TEST_P12_PASSWD);
//        }
        ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        Enumeration<String> alias=ks.aliases();
        while (alias.hasMoreElements()){
            String aliass=alias.nextElement();
            Certificate cert1=ks.getCertificateChain(aliass)[0];
            X509Certificate cert= (X509Certificate) cert1;
            if(cert.getSubjectDN().toString().equals(keyname)&&ks.getKey(aliass,TEST_P12_PASSWD)!=null){
                privateKey=(PrivateKey)ks.getKey(aliass,TEST_P12_PASSWD);
                break;
            }
        }

        return privateKey;

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


    private static X509Certificate findSignedCert(X509Certificate signingCert, List<X509Certificate> certificates)
    {
        X509Certificate signed = null;
        for (X509Certificate cert : certificates)
        {
            Principal signingCertSubjectDN = signingCert.getSubjectDN();
            Principal certIssuerDN = cert.getIssuerDN();
            if (certIssuerDN.equals(signingCertSubjectDN) && !cert.equals(signingCert))
            {
                signed = cert;
                break;
            }
        }
        return signed;
    }


    private static X509Certificate findSignerCertificate(X509Certificate signedCert, List<X509Certificate> certificates) {
        X509Certificate signer = null;
        for (X509Certificate cert : certificates) {
            Principal certSubjectDN = cert.getSubjectDN();
            Principal issuerDN = signedCert.getIssuerDN();
            if (certSubjectDN.equals(issuerDN)) {
                signer = cert;
                break;
            }
        }
        return signer;
    }

    private static X509Certificate findRootCert(List<X509Certificate> certificates) {
        X509Certificate rootCert = null;
        for (X509Certificate cert : certificates) {
            X509Certificate signer = findSignerCertificate(cert, certificates);
            if (signer == null || signer.equals(cert)) {
                rootCert = cert;
                break;
            }
        }
        return rootCert;
    }

    private static boolean checkCertChain(Certificate[] sortedchain) throws CertificateNotYetValidException, CertificateExpiredException {
        Boolean isValidCertChain=true;
        for (int j=0;j<sortedchain.length;j++){
            X509Certificate cert= (X509Certificate) sortedchain[j];
            cert.checkValidity();
            if (!(cert.getNotBefore().getTime()<System.currentTimeMillis()&&System.currentTimeMillis()<cert.getNotAfter().getTime())){
                System.out.println(cert.getSubjectDN()+"证书过期!!!");
            }

            if(j< sortedchain.length-1){
                X509Certificate nextcert1=(X509Certificate) sortedchain[j+1];
                BCECPublicKey bcRootPub = SM2CertUtil.getBCECPublicKey(nextcert1);
                if (!SM2CertUtil.verifyCertificate(bcRootPub, cert)){
                    isValidCertChain=false;
                }

            }else if(j==sortedchain.length-1){
                X509Certificate nextcert1=(X509Certificate) sortedchain[j];
                BCECPublicKey bcRootPub = SM2CertUtil.getBCECPublicKey(nextcert1);
                if (!SM2CertUtil.verifyCertificate(bcRootPub, cert)){
                    isValidCertChain=false;
                }
            }
        }
        return isValidCertChain;
    }

    public String jsonDecrypt(String json) throws Exception{
        boolean checksign=true;
        JSONObject jsonObject = JSON.parseObject(json);
        String encryptkey=jsonObject.getString("Encrypted_Key");
        String KeyName=jsonObject.getString("KeyName");
        String encryptedText=jsonObject.getString("Protected");
        String signValue=jsonObject.getString("Signature");
        JSONObject certObject=jsonObject.getJSONObject("Certs");
        Iterator certiter = certObject.entrySet().iterator();
        X509Certificate[] chain=new X509Certificate[certObject.size()];
        int j=0;
        while(certiter.hasNext()){
            Map.Entry entry = (Map.Entry) certiter.next();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            byte[] certbcytes=Base64.getDecoder().decode(stringToBytes(entry.getValue().toString()));
            Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certbcytes));
            chain[j]=(X509Certificate) cert;
            j++;
        }
        List<X509Certificate> certificates=Arrays.asList(chain);
        Certificate[] sortedChain=new Certificate[certificates.size()];
        X509Certificate rootCert=findRootCert(certificates);
        X509Certificate nextCert=rootCert;
        for (int p=certificates.size()-1;p>=0;p--){
            sortedChain[p]=nextCert;
            nextCert=findSignedCert(nextCert,certificates);
        }

        Boolean certStatus=checkCertChain(sortedChain);
        if (!certStatus){
            System.out.println("证书链验证失败!!!");
        }
        final X509Certificate x509Certificate=(X509Certificate)sortedChain[0];
        PrivateKey privateKey=getPrivateKey(KeyName);
        byte[] thekey = null;
        try {
            thekey= Sm2Dec(encryptkey,privateKey);
            this.sm4key=thekey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String plainText=stringDecrypt(encryptedText);
        JSONObject js=JSON.parseObject(plainText);
        String temp=JSONObject.toJSONString(js, SerializerFeature.SortField.MapSortField);
        checksign=validate(temp,signValue,x509Certificate);

        System.out.println("验签结果:"+checksign+"\n");

        return temp;
    }

    public String jsonDecrypt1(String json) throws Exception{
        JSONObject jsonObject = JSON.parseObject(json);
        String encryptkey=jsonObject.getString("Encrypted_Key");
        String KeyName=jsonObject.getString("KeyName");
        String encryptedText=jsonObject.getString("Protected");
        PrivateKey privateKey=getPrivateKey(KeyName);
        byte[] thekey = null;
        try {
            thekey= Sm2Dec(encryptkey,privateKey);
            this.sm4key=thekey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String plainText=stringDecrypt(encryptedText);
        JSONObject js=JSON.parseObject(plainText);
        String temp=JSONObject.toJSONString(js, SerializerFeature.SortField.MapSortField);

        return temp;
    }

    public String jsonDecrypt2(String json) throws Exception{
        boolean checksign=true;
        JSONObject jsonObject = JSON.parseObject(json);
        String plainText=jsonObject.getString("Protected");
        String signValue=jsonObject.getString("Signature");
        JSONObject certObject=jsonObject.getJSONObject("Certs");
        Iterator certiter = certObject.entrySet().iterator();
        X509Certificate[] chain=new X509Certificate[certObject.size()];
        int j=0;
        while(certiter.hasNext()){
            Map.Entry entry = (Map.Entry) certiter.next();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            byte[] certbcytes=Base64.getDecoder().decode(stringToBytes(entry.getValue().toString()));
            Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certbcytes));
            chain[j]=(X509Certificate) cert;
            j++;
        }
        List<X509Certificate> certificates=Arrays.asList(chain);
        Certificate[] sortedChain=new Certificate[certificates.size()];
        X509Certificate rootCert=findRootCert(certificates);
        X509Certificate nextCert=rootCert;
        for (int p=certificates.size()-1;p>=0;p--){
            sortedChain[p]=nextCert;
            nextCert=findSignedCert(nextCert,certificates);
        }

        Boolean certStatus=checkCertChain(sortedChain);
        if (!certStatus){
            System.out.println("证书链验证失败!!!");
        }
        final X509Certificate x509Certificate=(X509Certificate)sortedChain[0];
        JSONObject js=JSON.parseObject(plainText);
        String temp=JSONObject.toJSONString(js, SerializerFeature.SortField.MapSortField);
        checksign=validate(temp,signValue,x509Certificate);

        System.out.println("验签结果:"+checksign+"\n");

        return temp;
    }
}

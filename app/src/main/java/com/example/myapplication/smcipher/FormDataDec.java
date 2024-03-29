package com.example.myapplication.smcipher;

import com.example.myapplication.CryptoConfigUtils;
import com.example.myapplication.MyApplication;
import com.example.myapplication.PropertiesUtils;
import com.example.myapplication.gmhelper.SM2Util;
import com.example.myapplication.gmhelper.SM4Util;
import com.example.myapplication.gmhelper.cert.SM2CertUtil;
import com.example.myapplication.gmhelper.cert.SM2X509CertMaker;
import com.example.myapplication.streamcipher.BCZuc;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FormDataDec {
    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();

    Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
    int encalg= new CryptoConfigUtils().getEncAlg(pro);
    String zuckey=null;
    byte[] sm4key = null;

    NodeMap nodeMap=new NodeMap();
    List<String> formenc=nodeMap.formreenc();
    List<String> formsign=nodeMap.formresign();

    public FormDataDec() throws IOException {
    }


    public  Map FormDataDec(Map<String, String> map) throws Exception {
        boolean checksign=true;
        String encryptkey=map.get("Encrypted_Key");
        System.out.println(encryptkey);
        String keyname=map.get("KeyName");
        System.out.println(keyname);
        PrivateKey privateKey=getPrivateKey(keyname);
        byte[] thekey = null;
        try {
            thekey= Sm2Dec(encryptkey,privateKey);
            if (encalg==0){
                this.sm4key=thekey;
            }else if(encalg==1){
                this.zuckey=bytesToString(thekey);
            }else {
                System.out.println("请检查对称加密配置!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.remove("Encrypted_Key");
        map.remove("KeyName");
        ArrayList arrayList=new ArrayList();
        List<X509Certificate> certificates = new ArrayList<>();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formenc.contains(entry.getKey())){
                entry.setValue(stringDecrypt(entry.getValue()));
            }
            if(entry.getKey().contains("cert")){
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                byte[] certbcytes=Base64.getDecoder().decode(stringToBytes(entry.getValue().toString()));
                Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certbcytes));
                certificates.add((X509Certificate) cert);
                arrayList.add(entry.getKey());
//                map.remove(entry.getKey());
            }
        }

        for(Object string:arrayList){
            map.remove(string);
        }
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
        ArrayList signList=new ArrayList();
        Iterator<Map.Entry<String, String>> signEntry = map.entrySet().iterator();
        while(signEntry.hasNext()){
            Map.Entry<String, String> entry = signEntry.next();
            if(entry.getKey().contains("-sig")){
                String s=entry.getKey().substring(0,entry.getKey().length()-4);
                if(!validate(map.get(s),entry.getValue(),x509Certificate)){
                    checksign=false;
                    System.out.println(s+"验签失败！！！");
                }
                signList.add(entry.getKey());
//                map.remove(entry.getKey());
            }
        }
        for(Object string:signList){
            map.remove(string);
        }

        System.out.println("验签结果:"+checksign);
        return map;
    }

    public  Map FormDataDec1(Map<String, String> map) throws Exception {
        String encryptkey=map.get("Encrypted_Key");
        String keyname=map.get("KeyName");
        PrivateKey privateKey=getPrivateKey(keyname);
        byte[] thekey = null;
        try {
            thekey= Sm2Dec(encryptkey,privateKey);
            if (encalg==0){
                this.sm4key=thekey;
            }else if(encalg==1){
                this.zuckey=bytesToString(thekey);
            }else {
                System.out.println("请检查对称加密配置!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.remove("Encrypted_Key");
        map.remove("KeyName");
        List<X509Certificate> certificates = new ArrayList<>();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formenc.contains(entry.getKey())){
                entry.setValue(stringDecrypt(entry.getValue()));
            }
        }
        return map;
    }

    public  Map FormDataDec2(Map<String, String> map) throws Exception {
        boolean checksign=true;
        List<X509Certificate> certificates = new ArrayList<>();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if(formenc.contains(entry.getKey())){
                entry.setValue(stringDecrypt(entry.getValue()));
            }
            if(entry.getKey().contains("cert")){
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                byte[] certbcytes=Base64.getDecoder().decode(stringToBytes(entry.getValue().toString()));
                Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certbcytes));
                certificates.add((X509Certificate) cert);
                map.remove(entry.getKey());
            }
        }

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

        Iterator<Map.Entry<String, String>> signEntry = map.entrySet().iterator();
        while(signEntry.hasNext()){
            Map.Entry<String, String> entry = signEntry.next();
            if(entry.getKey().contains("-sig")){
                String s=entry.getKey().substring(0,entry.getKey().length()-4);
                if(!validate(map.get(s),entry.getValue(),x509Certificate)){
                    checksign=false;
                    System.out.println(s+"验签失败！！！");
                }
                map.remove(entry.getKey());
            }
        }

        System.out.println("验签结果:"+checksign);
        return map;
    }





    public String stringDecrypt(String text) throws BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException {
        if(encalg==0){
            byte[] cipherdata=stringToBytes(text);
            byte[] iv=Base64.getDecoder().decode(stringToBytes("LvbTKayS1A2NFFBjaPvkJg=="));
            byte[] decryptedData= SM4Util.decrypt_CBC_Padding(sm4key,iv,Base64.getDecoder().decode(cipherdata));
            return bytesToString(decryptedData);
        }else if(encalg==1){
            String res=new BCZuc().stringDecCipher(text,zuckey);
            return res;
        }else {
            System.out.println("请检查对称加密配置!!!");
            return null;
        }
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static byte[] Sm2Dec(String text, PrivateKey privateKey) throws InvalidCipherTextException {
        byte[] aa=Base64.getDecoder().decode(stringToBytes(text));
        byte[] decryptdata= SM2Util.decrypt((BCECPrivateKey)privateKey,aa);
        return decryptdata;
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

            //出现bug 并解决
            //在使用BASE64字符串作为参数在传递的时候，其中的“+”会被解析成空格，这在解码的时候会出现问题，在解析前将空格重新替换成“+”即可。
            //参考https://blog.csdn.net/qq_35540187/article/details/115209070
            str = str.replaceAll(" ", "+");
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
}

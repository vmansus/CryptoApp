//package com.example.myapplication.smcipher.sm2;
//
//import com.example.myapplication.smcipher.Util;
//
//import java.io.IOException;
//
//public class SM2test {
//    //公钥加密
//    public static String SM2Enc(String pubKey, String src) throws IOException {
//        String encrypt = SM2EncDecUtils.encrypt(Util.hexStringToBytes(pubKey), src.getBytes());
//        //删除04
//        encrypt=encrypt.substring(2,encrypt.length());
//        return encrypt;
//    }
//
//    //私钥解密
//    public static String SM2Dec(String priKey, String encryptedData) throws IOException {
//        //填充04
//        encryptedData="04"+encryptedData;
//        byte[] decrypt = SM2EncDecUtils.decrypt(Util.hexStringToBytes(priKey), Util.hexStringToBytes(encryptedData));
//        return new String(decrypt);
//    }
//    //私钥签名,参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
//    public static SM2SignVO genSM2Signature(String priKey, String sourceData) throws Exception {
//        SM2SignVO sign = SM2SignVerUtils.Sign2SM2(Util.hexToByte(priKey), Util.hexToByte(sourceData));
//        return sign;
//    }
//
//    //公钥验签,参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
//    public static boolean verifySM2Signature(String pubKey, String sourceData, String hardSign) {
//        SM2SignVO verify = SM2SignVerUtils.VerifySignSM2(Util.hexStringToBytes(pubKey), Util.hexToByte(sourceData), Util.hexToByte(hardSign));
//        return verify.isVerify();
//    }
//
//}

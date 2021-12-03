//package com.example.myapplication.smcipher;
//
//import com.example.myapplication.smcipher.sm2.SM2test;
//
//public class cryptoutils {
//    public static final String privateKey = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";
//    public static final String publicKey = "04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D87F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";
//
//    public static String stringSign(String text) throws Exception {
//        String aa=Util.byteToHex(text.getBytes());
////        String sign=RSA.sign(text,privateKey);
////        return sign;
//        String sign= SM2test.genSM2Signature(privateKey,aa).getSm2_signForSoft();
//        return sign;
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        String ss=stringSign("aabbvv");
//        System.out.println(ss);
//    }
//
//}

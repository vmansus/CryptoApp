//package com.example.myapplication.smcipher;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.example.myapplication.smcipher.sm2.SM2SignVO;
//import com.example.myapplication.smcipher.sm2.SM2SignVerUtils;
//import com.example.myapplication.smcipher.sm2.SM2test;
//import com.example.myapplication.smcipher.sm4.SM4Utils;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class FormDataEnc {
//    public static final String privateKey = "53cb2ba32c6e8389709ab7b3db297f7374075214d303bd48a1e7457faf1dfc0c";
//    public static final String publicKey = "043d6a94d8bf6ecba363e0cee4302d372ddf3737bfc2cb6b4afb761463dcecbcae949999db1cbc1d7c903fbb2a52d49a4915bd6e3c57efce5bec65100d90c557cf";
//
//    String sm4key= UUID.randomUUID().toString().replace("-", "");
//
//    NodeMap nodeMap=new NodeMap();
//    List<String> formenc=nodeMap.formenc();
//    List<String> formsign=nodeMap.formsign();
//    public Map<String, Object> signedmap=new HashMap<>();
//
//    public String stringEncrypt(String text) {
//        SM4Utils sm4 = new SM4Utils();
//        sm4.secretKey = sm4key;
//        sm4.hexString = true;
//        sm4.iv = "31313131313131313131313131313131";
//        String cipherText = sm4.encryptData_CBC(text);
//        return cipherText;
//
//    }
//
//    public String stringSign(String text) throws Exception {
//        String aa= Util.byteToHex(text.getBytes());
//        SM2SignVO sign = genSM2Signature(privateKey, aa);
//        return sign.getSm2_signForSoft();
//
//    }
//
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
//    public String FormDataEnc(Map<String, String> map) throws Exception {
//        String encryptkey = null;
//        try {
//            encryptkey = SM2test.SM2Enc(publicKey,sm4key);
////            System.out.println(encryptkey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JSONObject jsonObject=new JSONObject();
//        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, String> entry = it.next();
//            if(formsign.contains(entry.getKey())){
//                this.signedmap.put(entry.getKey(),stringSign(entry.getValue()));
//            }
//            if(formenc.contains(entry.getKey())){
//                entry.setValue(stringEncrypt(entry.getValue()));
//            }
//
//            jsonObject.put(entry.getKey(),entry.getValue());
//        }
//        jsonObject.put("encryptkey",encryptkey);
//        String temp=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);
//        String json2=getJsonNew(temp,signedmap);
//        return json2;
//    }
//
//    public static String getJsonNew (String jsonStrO , Map<String, Object> map){
//        if(org.apache.commons.lang.StringUtils.isBlank(jsonStrO)){
//            jsonStrO = "{}";
//        }
//
//        if(map == null || map.isEmpty()){
//            return jsonStrO;
//        }
//
//        String jsonStrN = "";
//        JSONObject json = JSONObject.parseObject(jsonStrO);
//        Map<String, Object> mapO = (Map<String, Object>)json;
//        mapO.put("Signature",map);
//
//        JSONObject jsonN = new JSONObject(mapO);
//        jsonStrN=JSONObject.toJSONString(jsonN, SerializerFeature.SortField.MapSortField);
//
//        return jsonStrN;
//    }
//}

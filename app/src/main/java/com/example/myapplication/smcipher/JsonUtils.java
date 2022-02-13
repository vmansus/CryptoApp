package com.example.myapplication.smcipher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.myapplication.CryptoConfigUtils;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApplication;
import com.example.myapplication.PropertiesUtils;
import com.example.myapplication.gmhelper.SM2Util;
import com.example.myapplication.gmhelper.SM4Util;
import com.example.myapplication.gmhelper.cert.SM2X509CertMaker;
import com.example.myapplication.streamcipher.BCZuc;
import com.example.myapplication.streamcipher.RandomZucKeyGenerater;

//import com.example.myapplication.smcipher.sm4.SM4Utils;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//@Component
public class JsonUtils {



    static {
        Security.removeProvider("SunEC");
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final char[] TEST_P12_PASSWD="12345678".toCharArray();

//    private static final String TEST_P12_FILENAME="C:\\Users\\20781\\AndroidStudioProjects\\MyApplication\\app\\src\\main\\res\\clientkeystore.p12";


    private static JsonUtils instance=null;
    private Key keyEncryptKey=null;


    public JsonUtils(X509Certificate certificate) throws NoSuchProviderException, NoSuchAlgorithmException, IOException {
        keyEncryptKey=certificate.getPublicKey();
    }

    public  static  JsonUtils getInstance(X509Certificate certificate) throws Exception{
        if (instance==null)return new JsonUtils(certificate);
        else return instance;
    }

    Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
    int encalg= new CryptoConfigUtils().getEncAlg(pro);
    byte[] sm4key= SM4Util.generateKey();
    String zuckey=new RandomZucKeyGenerater().makeKey();




    NodeMap nodeMap=new NodeMap();
    Map<String, List<String>> encryptNodeMap=nodeMap.encryptNodeMap();
    Map<String, List<String>> signNodeMap=nodeMap.signNodeMap();
    public Map<String, Object> signedmap=new HashMap<>();

    //   Map<String, Object> signmap1=new HashMap();

    /**
     * 文本加密（忽略异常）
     *
     * @param text 入参
     * @return 加密字符串
     */
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
        KeyStore  ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        PrivateKey privateKey=(BCECPrivateKey)ks.getKey(alias,TEST_P12_PASSWD);

        byte[] srcData = text.getBytes();
        Signature sign = Signature.getInstance(SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2, "BC");
        sign.initSign(privateKey);
        sign.update(srcData);
        byte[] signatureValue = sign.sign();

        return bytesToString(Base64.getEncoder().encode(signatureValue));
    }

    public static String Sm2Enc(byte[] srcData,Key publickey) throws InvalidCipherTextException {
        byte[] ciperdata= SM2Util.encrypt((BCECPublicKey) publickey,srcData);
        String ss=bytesToString(Base64.getEncoder().encode(ciperdata));
        return ss;
    }


    public java.security.cert.Certificate[] getCerts(String alias) throws Exception{
        KeyStore  ks= PropertiesUtils.getkeyStore(MyApplication.getContext());
        Certificate[] certificates=ks.getCertificateChain(alias);
        return certificates;

    }

    /**
     * json指定节点加密
     *
     * @param json 入参
     * @return 加密字符串
     */
    public String  jsonEncrypt(String json,String keyname,String alias) {

        String json2 = "";
        System.out.println(json);
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key),alias);
                    String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();

                    Map<String, Object> signature =this.signedmap;
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
                    JSONObject jsonObject = JSON.parseObject(output);
                    jsonObject.put("Encrypted_Key",encryptkey);
                    jsonObject.put("KeyName",keyname);

                    JSONObject jsonChainCert=new JSONObject();
                    for (int i=0;i<getCerts(alias).length;i++){
                        jsonChainCert.put(String.valueOf(i),bytesToString(Base64.getEncoder().encode(getCerts(alias)[i].getEncoded())));
                    }

//                    X509Certificate cert= (X509Certificate) getCerts(alias)[0];
                    jsonObject.put("Certs",jsonChainCert);

                    String result=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);
//                    System.out.println(result);
                    //1、迭代器
                    Iterator<Map.Entry<String, Object>> iter = signature.entrySet().iterator();
                    //判断往下还有没有数据
                    while(iter.hasNext()){
                        //有的话取出下面的数据
                        Map.Entry<String, Object> entry = iter.next();
                        Object key1 = entry.getKey();
                        String value = (String)entry.getValue();

//                        System.out.println(key1 + " ：" + value);
                    }
                    json2=getJsonNew(result,signature);
//                    System.out.println(result);
                }
            }
        } catch (Exception e) {
            String output = "json加密异常:" + e.getMessage() + "加密前信息：" + json;
            json2=output;

        }
        return json2;
    }

    public String  jsonEncryptmode1(String json,String keyname,String alias) {

        String json2 = "";
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key),alias);
                    String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();
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
                    JSONObject jsonObject = JSON.parseObject(output);
                    jsonObject.put("Encrypted_Key",encryptkey);
                    jsonObject.put("KeyName",keyname);
                    String result=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);
                    json2=result;
                }
            }
        } catch (Exception e) {
            String output = "json加密异常:" + e.getMessage() + "加密前信息：" + json;
            json2=output;

        }
        return json2;
    }

    public String  jsonEncryptmode2(String json,String keyname,String alias) {

        String json2 = "";
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key),alias);
                    //String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();

                    Map<String, Object> signature =this.signedmap;
//                    GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).si
                    // 使用RSA算法将随机生成的AESkey加密
                    String encryptkey = null;
                    try {
                        encryptkey = Sm2Enc(sm4key,keyEncryptKey);
//                        System.out.println(encryptkey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = JSON.parseObject(json);

                    JSONObject jsonChainCert=new JSONObject();
                    for (int i=0;i<getCerts(alias).length;i++){
                        jsonChainCert.put(String.valueOf(i),bytesToString(Base64.getEncoder().encode(getCerts(alias)[i].getEncoded())));
                    }
//                    X509Certificate cert= (X509Certificate) getCerts(alias)[0];
                    jsonObject.put("Certs",jsonChainCert);

//                    jsonObject.put("encryptkey",encryptkey);
                    String result=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);
//                    System.out.println(result);

                    json2=getJsonNew(result,signature);
                }
            }
        } catch (Exception e) {
            String output = "json加密异常:" + e.getMessage() + "加密前信息：" + json;
            json2=output;

        }
        return json2;
    }



    public static String getJsonNew (String jsonStrO , Map<String ,Object> map){
        if(StringUtils.isBlank(jsonStrO)){
            jsonStrO = "{}";
        }

        if(map == null || map.isEmpty()){
            return jsonStrO;
        }

        String jsonStrN = "";
        JSONObject json = JSONObject.parseObject(jsonStrO);
        Map<String, Object> mapO = (Map<String, Object>)json;
        mapO.put("Signature",map);

        JSONObject jsonN = new JSONObject(mapO);
        jsonStrN=JSONObject.toJSONString(jsonN,SerializerFeature.SortField.MapSortField);

        return jsonStrN;
    }




    /**
     * 根据节点逐一展开json对象并进行加密
     *
     * @param object   入参
     * @param nodeList 入参
     * @return 结果
     */
    public Object GetAesJToken(Object object, List<String> nodeList) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, UnsupportedEncodingException {


        // 如果为空，直接返回
        if (object == null || nodeList.size() == 0) return object;
        JSONObject jsonObject = null;
        // 多层节点递归展开，单层节点直接加密
        Map<String, List<String>> deepLevelNodes = new HashMap<>();
        for (var node : nodeList) {
            var nodeArr = Arrays.asList(node.split("\\."));
            if (nodeArr.size() > 1) {
                if (deepLevelNodes.containsKey(nodeArr.get(0)))
                    deepLevelNodes.get(nodeArr.get(0))
                            .add(com.ctrip.framework.apollo.core.utils.StringUtils
                                    .join(nodeArr.subList(1, nodeArr.size()), "."));
                else
                    deepLevelNodes.put(nodeArr.get(0),
                            new ArrayList<>(Arrays.asList(com.ctrip.framework.apollo.core.utils.StringUtils
                                    .join(nodeArr.subList(1, nodeArr.size()), "."))));
            } else {
                object = JsonNodeToAes(object, node);

            }
        }


        if (deepLevelNodes.size() > 0) {
            for (String key : deepLevelNodes.keySet()) {
                //JSONValidator validator = JSONValidator.from(x);
                if (
//                        JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
                        JSON.isValidObject(object.toString())
                ) {
                    var jObject = JSON.parseObject(object.toString());
                    if (jObject.get(key) != null) {
                        jObject.put(key, GetAesJToken(jObject.get(key), deepLevelNodes.get(key)));
                    }
                    object = jObject;
                }
                if (
//                        JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Array
                        JSON.isValidArray(object.toString())
                ) {
                    var jArray = JSON.parseArray(object.toString());
                    for (int i = 0; i < jArray.size(); i++) {
                        JSONObject curObject = jArray.getJSONObject(i);
                        if (curObject != null && curObject.get(key) != null) {
                            jArray.set(i, GetAesJToken(curObject.get(key), deepLevelNodes.get(key)));
                        }
                    }
                    object = jArray;
                }
            }
        }
        return object;
    }


    public Object  GetJsonSign(Object object, List<String> nodeList,String alias) throws Exception {
        Map<String, Object> signmap=new HashMap();
        // 如果为空，直接返回
        if (object == null || nodeList.size() == 0) return null;
        JSONObject jsonObject = null;
        // 多层节点递归展开，单层节点直接加密
        Map<String, List<String>> deepLevelNodes = new HashMap<>();
        for (var node : nodeList) {
            var nodeArr = Arrays.asList(node.split("\\."));
            if (nodeArr.size() > 1) {
                if (deepLevelNodes.containsKey(nodeArr.get(0)))
                    deepLevelNodes.get(nodeArr.get(0))
                            .add(com.ctrip.framework.apollo.core.utils.StringUtils
                                    .join(nodeArr.subList(1, nodeArr.size()), "."));
                else
                    deepLevelNodes.put(nodeArr.get(0),
                            new ArrayList<>(Arrays.asList(com.ctrip.framework.apollo.core.utils.StringUtils
                                    .join(nodeArr.subList(1, nodeArr.size()), "."))));
            } else {
                //object = JsonNodeToAes(object, node);

                object=JsonNodeSign(object, node,alias);

            }
        }


        if (deepLevelNodes.size() > 0) {
            for (String key : deepLevelNodes.keySet()) {
                //JSONValidator validator = JSONValidator.from(x);
                if (
                        JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
                    //JSON.isValidObject(object.toString())
                ) {
                    var jObject = JSON.parseObject(object.toString());
                    if (jObject.get(key) != null) {
                        jObject.put(key, GetJsonSign(jObject.get(key), deepLevelNodes.get(key),alias));
                    }
                    object = jObject;
                }
                if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Array
                    //JSON.isValidArray(object.toString())
                ) {
                    var jArray = JSON.parseArray(object.toString());
                    for (int i = 0; i < jArray.size(); i++) {
                        JSONObject curObject = jArray.getJSONObject(i);
                        if (curObject != null && curObject.get(key) != null) {
                            jArray.set(i, GetJsonSign(curObject.get(key), deepLevelNodes.get(key),alias));
                        }
                    }
                    object = jArray;
                }
            }
        }
        return object;
    }


    /**
     * 将确定节点加密
     *
     * @param object 入参
     * @param node   入参
     * @return 结果
     */
    private Object JsonNodeToAes(Object object, String node) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        if (object == null) return object;
        if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
            // JSON.isValidObject(object.toString())
        ) {
            var jObject = JSON.parseObject(object.toString());
            if (jObject.get(node) != null) {
                if (JSONValidator.from(jObject.get(node).toString()).getType()==JSONValidator.Type.Array
                    //JSON.isValidArray(jObject.get(node).toString())
                ) {
                    var jArray = jObject.getJSONArray(node);
                    for (int i = 0; i < jArray.size(); i++) {
                        jArray.set(i, stringEncrypt(jArray.get(i).toString()));
                    }
                    jObject.put(node, jArray);
                } else
//                    if (
//                            JSONValidator.from(jObject.get(node).toString()).getType()!=JSONValidator.Type.Object    //非
////                        !JSON.isValidObject(jObject.get(node).toString())
//                )
                {
                    jObject.put(node, stringEncrypt(jObject.get(node).toString()));
                }
            }
            object = jObject;
        } else if (
                JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Array
            //JSON.isValidArray(object.toString())
        ) {
            var jArray = JSON.parseArray(object.toString());
            for (int i = 0; i < jArray.size(); i++) {
                Object curObject = jArray.getJSONObject(i);
                if (curObject != null) {
                    jArray.set(i, JsonNodeToAes(curObject, node));
                }
            }
            object = jArray;
        } else {
            object = stringEncrypt(object.toString());
        }
        return object;
    }


    /**
     * 将确定节点签名
     *
     * @param object 入参
     * @param node   入参
     * @return 结果
     */
    private Object JsonNodeSign(Object object, String node,String alias) throws Exception {
        if (object == null) return null;

        if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
            // JSON.isValidObject(object.toString())
        ) {
            var jObject = JSON.parseObject(object.toString());
            if (jObject.get(node) != null) {
                if (JSONValidator.from(jObject.get(node).toString()).getType()==JSONValidator.Type.Array
                    //JSON.isValidArray(jObject.get(node).toString())
                ) {
                    var jArray = jObject.getJSONArray(node);
                    for (int i = 0; i < jArray.size(); i++) {
                        jArray.set(i, jArray.get(i).toString());

                        String tmp=JSONObject.toJSONString(jArray.get(i),SerializerFeature.SortField.MapSortField);
                        this.signedmap.put(node, stringSign(tmp,alias));

//                        this.signedmap.put(node, stringSign(jArray.get(i).toString()));
                    }
                    jObject.put(node, jArray);
                } else
//                    if (JSONValidator.from(jObject.get(node).toString()).getType()!=JSONValidator.Type.Object    //非
//                    //!JSON.isValidObject(jObject.get(node).toString())
//                )
                {
                    jObject.put(node, jObject.get(node).toString());

                    String tmp=JSONObject.toJSONString(jObject.get(node),SerializerFeature.SortField.MapSortField);
                    this.signedmap.put(node, stringSign(tmp,alias));

//                    this.signedmap.put(node, stringSign(jObject.get(node).toString()));
                }
            }
            object = jObject;
        } else if (
                JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Array
            //JSON.isValidArray(object.toString())
        ) {
            var jArray = JSON.parseArray(object.toString());
            for (int i = 0; i < jArray.size(); i++) {
                Object curObject = jArray.getJSONObject(i);
                if (curObject != null) {
                    jArray.set(i, JsonNodeSign(curObject, node,alias));
                }
            }
            object = jArray;
//            signmap = jArray.;
        } else {
            object = object.toString();

            String tmp=JSONObject.toJSONString(object,SerializerFeature.SortField.MapSortField);
            this.signedmap.put(node, stringSign(tmp,alias));

//            this.signedmap.put(node,object.toString());
        }


        return object;


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

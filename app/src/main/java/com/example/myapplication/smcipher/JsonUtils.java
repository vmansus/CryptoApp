package com.example.myapplication.smcipher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.myapplication.smcipher.sm2.SM2SignVO;
import com.example.myapplication.smcipher.sm2.SM2SignVerUtils;
import com.example.myapplication.smcipher.sm2.SM2test;
import com.example.myapplication.smcipher.sm4.SM4Utils;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
public class JsonUtils {



    public static final String privateKey = "53cb2ba32c6e8389709ab7b3db297f7374075214d303bd48a1e7457faf1dfc0c";
    public static final String publicKey = "043d6a94d8bf6ecba363e0cee4302d372ddf3737bfc2cb6b4afb761463dcecbcae949999db1cbc1d7c903fbb2a52d49a4915bd6e3c57efce5bec65100d90c557cf";

    String sm4key= UUID.randomUUID().toString().replace("-", "");

    String signstr;


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
    public String stringEncrypt(String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = sm4key;
        sm4.hexString = true;
        sm4.iv = "31313131313131313131313131313131";
        String cipherText = sm4.encryptData_CBC(text);
        return cipherText;
//        try {
//            if (!StringUtils.isBlank(text)) {
//                text = AES.encryptToBase64(ConvertUtils.stringToHexString(text), aesKey);
//            }
//        } catch (Exception e) {
//            text = "文本加密异常:" + e.getMessage() + "加密前信息：" + text;
//        }
//        return text;
    }

    public String stringSign(String text) throws Exception {
        String aa= Util.byteToHex(text.getBytes());
//        String sign=RSA.sign(text,privateKey);
//        return sign;
        SM2SignVO sign = genSM2Signature(privateKey, aa);
        return sign.getSm2_signForSoft();

    }

    //私钥签名,参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
    public static SM2SignVO genSM2Signature(String priKey, String sourceData) throws Exception {
        SM2SignVO sign = SM2SignVerUtils.Sign2SM2(Util.hexToByte(priKey), Util.hexToByte(sourceData));
        return sign;
    }

    //公钥验签,参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
    public static boolean verifySM2Signature(String pubKey, String sourceData, String hardSign) {
        SM2SignVO verify = SM2SignVerUtils.VerifySignSM2(Util.hexStringToBytes(pubKey), Util.hexToByte(sourceData), Util.hexToByte(hardSign));
        return verify.isVerify();
    }

    /**
     * json指定节点加密
     *
     * @param json 入参
     * @return 加密字符串
     */
    public String jsonEncrypt(String json) {

        String json2 = "";
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key));
                   String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();

                    Map<String, Object> signature =this.signedmap;
//                    GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).si
                    String encryptkey = null;
                    try {
                        encryptkey = SM2test.SM2Enc(publicKey,sm4key);
//                        System.out.println(encryptkey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = JSON.parseObject(output);
                    jsonObject.put("encryptkey",encryptkey);
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

    public String jsonEncryptmode1(String json) {

        String json2 = "";
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key));
                    String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();
                    String encryptkey = null;
                    try {
                        encryptkey = SM2test.SM2Enc(publicKey,sm4key);
//                        System.out.println(encryptkey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = JSON.parseObject(output);
                    jsonObject.put("encryptkey",encryptkey);
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

    public String jsonEncryptmode2(String json) {

        String json2 = "";
        try {
            if (!StringUtils.isBlank(json)) {
                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    GetJsonSign(JSON.parseObject(json.trim()), signNodeMap.get(key));
                    String  output = GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).toString();

                    Map<String, Object> signature =this.signedmap;
//                    GetAesJToken(JSON.parseObject(json.trim()), encryptNodeMap.get(key)).si
                    // 使用RSA算法将随机生成的AESkey加密
                    String encryptkey = null;
                    try {
                        encryptkey = SM2test.SM2Enc(publicKey,sm4key);
//                        System.out.println(encryptkey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = JSON.parseObject(json);
//                    jsonObject.put("encryptkey",encryptkey);
                    String result=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField);

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
        if(org.apache.commons.lang.StringUtils.isBlank(jsonStrO)){
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
    public Object GetAesJToken(Object object, List<String> nodeList) {


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


    public Object  GetJsonSign(Object object, List<String> nodeList) throws Exception {
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

                object=JsonNodeSign(object, node);

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
                        jObject.put(key, GetJsonSign(jObject.get(key), deepLevelNodes.get(key)));
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
                            jArray.set(i, GetJsonSign(curObject.get(key), deepLevelNodes.get(key)));
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
    private Object JsonNodeToAes(Object object, String node) {
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
    private Object JsonNodeSign(Object object, String node) throws Exception {
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
                        this.signedmap.put(node, stringSign(tmp));

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
                    this.signedmap.put(node, stringSign(tmp));

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
                    jArray.set(i, JsonNodeSign(curObject, node));
                }
            }
            object = jArray;
//            signmap = jArray.;
        } else {
            object = object.toString();

            String tmp=JSONObject.toJSONString(object,SerializerFeature.SortField.MapSortField);
            this.signedmap.put(node, stringSign(tmp));

//            this.signedmap.put(node,object.toString());
        }


        return object;


    }





}

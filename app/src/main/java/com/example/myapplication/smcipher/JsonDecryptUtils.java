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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JsonDecryptUtils {
    public static final String privateKey = "53cb2ba32c6e8389709ab7b3db297f7374075214d303bd48a1e7457faf1dfc0c";
    public static final String publicKey = "043d6a94d8bf6ecba363e0cee4302d372ddf3737bfc2cb6b4afb761463dcecbcae949999db1cbc1d7c903fbb2a52d49a4915bd6e3c57efce5bec65100d90c557cf";



    String sm4key = null;

    // 需要加密的日志节点
//    HashMap<String, List<String>> encryptNodeMap = new HashMap<String, List<String>>();
        // 需要加密的日志节点
    NodeMap nodeMap=new NodeMap();
    Map<String, List<String>> encryptNodeMap=nodeMap.reencNodeMap();
    List<String> signNodelist=nodeMap.resignlist();
    public Map<String, Object> signvaluemap=new HashMap<>();







    public String jsonDecrypt(String json){
        boolean checksign=true;
        String json2 = "";
        String jsonStr="";
        try {
            if (!StringUtils.isBlank(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
                String encryptkey=jsonObject.getString("encryptkey");
                Map<Object,Object>  signature= (Map<Object, Object>) jsonObject.get("Signature");
                jsonObject.remove("Signature");
                String json1=jsonObject.toJSONString();
                // 使用SM2算法将随机生成的SM4key解密
                String thekey = null;
                try {
                    thekey= SM2test.SM2Dec(privateKey,encryptkey);
                    this.sm4key=thekey;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    Object output = GetAesJToken(JSON.parseObject(json1.trim()), encryptNodeMap.get(key));
                    JSONObject jsonObject1 =  (JSONObject) JSON.toJSON(output);
                    jsonObject1.remove("encryptkey");
                    String result=JSONObject.toJSONString(jsonObject1, SerializerFeature.SortField.MapSortField,SerializerFeature.DisableCheckSpecialChar);

                    jsonStr = StringEscapeUtils.unescapeJavaScript(result);
                    //json2=jsonStr;
//                    System.out.println(jsonStr);
                    for (int i = 0; i < jsonStr.length()-1; i++) {
                        if (jsonStr.charAt(i) =='"' && jsonStr.charAt(i+1) =='{'  ) {
                            jsonStr=removeCharAt(jsonStr,i);
                        }else if(jsonStr.charAt(i) =='}' && jsonStr.charAt(i+1) =='"'){
                            jsonStr=removeCharAt(jsonStr,i+1);
                        }
                    }
//                    System.out.println(jsonStr+"11111");
                }
                GetSignvalue(JSON.parseObject(jsonStr.trim()),signNodelist);
                //1、迭代器
                Iterator<Map.Entry<String, Object>> iter = signvaluemap.entrySet().iterator();
                //判断往下还有没有数据
                while(iter.hasNext()){
                    //有的话取出下面的数据
                    Map.Entry<String, Object> entry = iter.next();
                    Object data = entry.getKey();
                    String signvalue = (String)entry.getValue();
                    String sign= (String) signature.get(data);
                    boolean b = verifySM2Signature(publicKey, Util.byteToHex(signvalue.getBytes()), sign);
//                    System.out.println(b);
                    if (!b){
                        checksign=false;
                        System.out.println(data+"验签失败");
                    }
                }
            }
        } catch (Exception e) {
            String output = "json解密异常:" + e.getMessage() + "解密前信息：" + json;
            jsonStr=output;

        }
        System.out.println(checksign);
        return jsonStr;

    }
    public String jsonDecryptmode1(String json){
        boolean checksign=true;
        String json2 = "";
        String jsonStr="";
        try {
            if (!StringUtils.isBlank(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
                String encryptkey=jsonObject.getString("encryptkey");
//                Map<Object,Object>  signature= (Map<Object, Object>) jsonObject.get("Signature");
//                jsonObject.remove("Signature");
                String json1=jsonObject.toJSONString();
                // 使用SM2算法将随机生成的SM4key解密
                String thekey = null;
                try {
                    thekey= SM2test.SM2Dec(privateKey,encryptkey);
                    this.sm4key=thekey;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
                    Object output = GetAesJToken(JSON.parseObject(json1.trim()), encryptNodeMap.get(key));
                    JSONObject jsonObject1 =  (JSONObject) JSON.toJSON(output);
                    jsonObject1.remove("encryptkey");
                    String result=JSONObject.toJSONString(jsonObject1, SerializerFeature.SortField.MapSortField,SerializerFeature.DisableCheckSpecialChar);

                    jsonStr = StringEscapeUtils.unescapeJavaScript(result);
                    for (int i = 0; i < jsonStr.length()-1; i++) {
                        if (jsonStr.charAt(i) =='"' && jsonStr.charAt(i+1) =='{'  ) {
                            jsonStr=removeCharAt(jsonStr,i);
                        }else if(jsonStr.charAt(i) =='}' && jsonStr.charAt(i+1) =='"'){
                            jsonStr=removeCharAt(jsonStr,i+1);
                        }
                    }
//                    System.out.println(jsonStr+"11111");
                }
            }
        } catch (Exception e) {
            String output = "json解密异常:" + e.getMessage() + "解密前信息：" + json;
            jsonStr=output;

        }
//        System.out.println(checksign);
        return jsonStr;

    }

    public String jsonDecryptmode2(String json){
        boolean checksign=true;
        String json2 = "";
        String jsonStr="";
        try {
            if (!StringUtils.isBlank(json)) {
                JSONObject jsonObject = JSON.parseObject(json);
//                String encryptkey=jsonObject.getString("encryptkey");
                Map<Object,Object>  signature= (Map<Object, Object>) jsonObject.get("Signature");
                jsonObject.remove("Signature");
//                String json1=jsonObject.toJSONString();
//                // 使用SM2算法将随机生成的SM4key解密
//                String thekey = null;
//                try {
//                    thekey= SM2test.SM2Dec(privateKey,encryptkey);
//                    this.sm4key=thekey;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                for (String key :  encryptNodeMap.keySet()){
//                    System.out.println(encryptNodeMap.get(key));
//                    Object output = GetAesJToken(JSON.parseObject(json1.trim()), encryptNodeMap.get(key));
//                    JSONObject jsonObject1 =  (JSONObject) JSON.toJSON(output);
//                    jsonObject1.remove("encryptkey");
                    String result=JSONObject.toJSONString(jsonObject, SerializerFeature.SortField.MapSortField,SerializerFeature.DisableCheckSpecialChar);

                    jsonStr = StringEscapeUtils.unescapeJavaScript(result);
                    //json2=jsonStr;
//                    System.out.println(jsonStr);
                    for (int i = 0; i < jsonStr.length()-1; i++) {
                        if (jsonStr.charAt(i) =='"' && jsonStr.charAt(i+1) =='{'  ) {
                            jsonStr=removeCharAt(jsonStr,i);
                        }else if(jsonStr.charAt(i) =='}' && jsonStr.charAt(i+1) =='"'){
                            jsonStr=removeCharAt(jsonStr,i+1);
                        }
                    }
//                    System.out.println(jsonStr+"11111");
//                }
                GetSignvalue(JSON.parseObject(jsonStr.trim()),signNodelist);
                //1、迭代器
                Iterator<Map.Entry<String, Object>> iter = signvaluemap.entrySet().iterator();
                //判断往下还有没有数据
                while(iter.hasNext()){
                    //有的话取出下面的数据
                    Map.Entry<String, Object> entry = iter.next();
                    Object data = entry.getKey();
                    String signvalue = (String)entry.getValue();
                    String sign= (String) signature.get(data);
                    boolean b = verifySM2Signature(publicKey, Util.byteToHex(signvalue.getBytes()), sign);
//                    System.out.println(b);
                    if (!b){
                        checksign=false;
                        System.out.println(data+"验签失败");
                    }
                }
            }
        } catch (Exception e) {
            String output = "json解密异常:" + e.getMessage() + "解密前信息：" + json;
            jsonStr=output;

        }
        System.out.println(checksign);
        return jsonStr;

    }

    /**
     * 文本解密（忽略异常）
     *
     * @param text 入参
     * @return 解密字符串
     */
    public String stringDecrypt(String text) {
        SM4Utils sm4 = new SM4Utils();
        sm4.secretKey = sm4key;
        sm4.hexString = true;
        sm4.iv = "31313131313131313131313131313131";
        String plainText = sm4.decryptData_CBC(text);
        return plainText;
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
//    public boolean checksign(String signvalue,String sign){
//        boolean b = verifySM2Signature(publicKey, Util.byteToHex(signvalue.getBytes()), sign);
//        return b;
//    }

    //公钥验签,参数二:原串必须是hex!!!!因为是直接用于计算签名的,可能是SM3串,也可能是普通串转Hex
    public static boolean verifySM2Signature(String pubKey, String sourceData, String hardSign) {

        SM2SignVO verify = SM2SignVerUtils.VerifySignSM2(Util.hexStringToBytes(pubKey), Util.hexToByte(sourceData), Util.hexToByte(hardSign));
        return verify.isVerify();
    }



    /**
     * 根据节点逐一展开json对象并进行解密
     *
     * @param object   入参
     * @param nodeList 入参
     * @return 结果
     */
    private Object GetAesJToken(Object object, List<String> nodeList) {
        // 如果为空，直接返回
        if (object == null || nodeList.size() == 0) return object;
        JSONObject jsonObject = null;
        // 多层节点递归展开，单层节点直接解密
        Map<String, List<String>> deepLevelNodes = new HashMap<>();
        for (var node : nodeList) {
            var nodeArr = Arrays.asList(node.split("\\."));
            if (nodeArr.size() > 1) {
                if (deepLevelNodes.containsKey(nodeArr.get(0)))
                    deepLevelNodes.get(nodeArr.get(0)).add(com.ctrip.framework.apollo.core.utils.StringUtils.join(nodeArr.subList(1, nodeArr.size()), "."));
                else
                    deepLevelNodes.put(nodeArr.get(0), new ArrayList<>(Arrays.asList(com.ctrip.framework.apollo.core.utils.StringUtils.join(nodeArr.subList(1, nodeArr.size()), "."))));
            } else {
                object = AesNodeToJson(object, node);
            }
        }
        if (deepLevelNodes.size() > 0) {
            for (String key : deepLevelNodes.keySet()) {
                //JSONValidator validator = JSONValidator.from(x);
                if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
                        //JSON.isValidObject(object.toString())
                ) {
                    var jObject = JSON.parseObject(object.toString());
                    if (jObject.get(key) != null) {
                        jObject.put(key, GetAesJToken(jObject.get(key), deepLevelNodes.get(key)));
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
                            jArray.set(i, GetAesJToken(curObject.get(key), deepLevelNodes.get(key)));
                        }
                    }
                    object = jArray;
                }
            }
        }
        return object;
    }




    /**
     * 将确定节点解密
     *
     * @param object 入参
     * @param node   入参
     * @return 结果
     */
    private Object AesNodeToJson(Object object, String node) {
        if (object == null) return object;
        if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
            // JSON.isValidObject(object.toString())
        ) {
            var jObject = JSON.parseObject(object.toString());
            if (jObject.get(node) != null) {
                if (
//                        JSONValidator.from(jObject.get(node).toString()).getType()==JSONValidator.Type.Array
                    JSON.isValidArray(jObject.get(node).toString())
                ) {
                    var jArray = jObject.getJSONArray(node);
                    for (int i = 0; i < jArray.size(); i++) {
                        jArray.set(i, stringDecrypt(jArray.get(i).toString()));
                    }
                    jObject.put(node, jArray);
                } else
//                    if (
////                            JSONValidator.from(jObject.get(node).toString()).getType()!=JSONValidator.Type.Object    //非
//                    !JSON.isValidObject(jObject.get(node).toString())
//                )
                    {
                        String TMP=stringDecrypt(jObject.get(node).toString());
//                        String TMP=JSONObject.toJSONString(jObject.get(node), SerializerFeature.SortField.MapSortField);
//                        System.out.println(TMP+"222");

//                        JSONObject jsonObject=JSONObject.parseObject(TMP);
//                        String tmp=JSONObject.toJSONString(jsonObject);
                    jObject.put(node, TMP);
                        //System.out.println(stringDecrypt(jObject.get(node).toString()));
                }
            }
            object = jObject;
        } else if (
//                JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Array
            JSON.isValidArray(object.toString())
        ) {
            var jArray = JSON.parseArray(object.toString());
            for (int i = 0; i < jArray.size(); i++) {
                Object curObject = jArray.getJSONObject(i);
                if (curObject != null) {
                    jArray.set(i, AesNodeToJson(curObject, node));
                }
            }
            object = jArray;
        } else {
            object = stringDecrypt(object.toString());
        }
        return object;
    }


    public Object  GetSignvalue(Object object, List<String> nodeList) throws Exception {
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
                if (JSONValidator.from(object.toString()).getType()==JSONValidator.Type.Object
                    //JSON.isValidObject(object.toString())
                ) {
                    var jObject = JSON.parseObject(object.toString());
                    if (jObject.get(key) != null) {
                        jObject.put(key, GetSignvalue(jObject.get(key), deepLevelNodes.get(key)));
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
                            jArray.set(i, GetSignvalue(curObject.get(key), deepLevelNodes.get(key)));
                        }
                    }
                    object = jArray;
                }
            }
        }
        return object;
    }

    /**
     * 确定节点验签
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

                        String tmp=JSONObject.toJSONString(jArray.get(i), SerializerFeature.SortField.MapSortField);
                        this.signvaluemap.put(node, tmp);
//                        this.signvaluemap.put(node, jArray.get(i).toString());
                    }
                    jObject.put(node, jArray);
                } else
//                    if (JSONValidator.from(jObject.get(node).toString()).getType()!=JSONValidator.Type.Object    //非
//                    //!JSON.isValidObject(jObject.get(node).toString())
//                )
                {
                    jObject.put(node, jObject.get(node).toString());
                    String tmp=JSONObject.toJSONString(jObject.get(node), SerializerFeature.SortField.MapSortField);
                    this.signvaluemap.put(node, tmp);
//                    this.signvaluemap.put(node,jObject.get(node).toString());
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
            String tmp=JSONObject.toJSONString(object, SerializerFeature.SortField.MapSortField);
            this.signvaluemap.put(node, tmp);
//            this.signvaluemap.put(node,object.toString());
        }


        return object;


    }
}
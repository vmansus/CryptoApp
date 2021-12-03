package com.example.myapplication;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSONObject;
//import com.example.myapplication.smcipher.FormDataEnc;
import com.example.myapplication.smcipher.JsonDecryptUtils;
import com.example.myapplication.smcipher.JsonUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;
import lombok.SneakyThrows;

public class AndroidWebServer extends NanoHTTPD {

//    public AndroidWebServer(int port) {
////        super(port);
////    }
////
////    public AndroidWebServer(String hostname, int port) {
////        super(hostname, port);
////    }
////
////    //...
//    Context context;

    private final static int PORT = 9000;


    public AndroidWebServer() throws IOException {
        super(PORT);
        start();
        System.out.println("\nRunning! Point your browers to http://localhost:9000/ \n");
    }

    @SneakyThrows
    @Override
    public Response serve(IHTTPSession session) {

        Properties pro = PropertiesUtils.getProperties(MyApplication.getContext());
//    String mode="2";
        String mode = pro.getProperty("workmode");
        System.out.println(mode);
//        if(session.getMethod().toString()== "OPTIONS"){
//            return null;
//        }
        String res = "";

        //String contenttype=session.;
        // System.out.println("dididid"+contenttype+"\n\n");


//        System.out.println(session.getUri());


//        try {
////            String encryptresponse=post("http://10.116.129.43:8787/body/testBody",encryptrequest);
//            String encryptresponse=post("http://10.0.2.2:8787/body/testBody",encryptrequest);
////            res=encryptresponse;
//            String decryptresponse=new JsonDecryptUtils().jsonDecrypt(encryptresponse);
//            res=decryptresponse;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //requestbody.put("flag","handled");

        Response response = null;


        String type = session.getMethod().toString();
        if (type.toUpperCase().equals("OPTIONS") == true) {
            response = newFixedLengthResponse(Response.Status.OK, "application/json", "ssuccess");
            response.addHeader("Access-Control-Allow-Headers", " X-Requested-With,Content-Type,Accept");
            response.addHeader("Access-Control-Allow-Methods", "POST");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setMimeType("text/plain;charset=utf-8");
            response.setStatus(Response.Status.OK);
            return response;

        }


//            return newFixedLengthResponse(Response.Status.OK,"application/json",encryptrequest);
        if (session.getUri().contains("/encJson")) {
            JSONObject requestbody = parseParms(session);
            String re = requestbody.toJSONString();
            System.out.println("re" + re);
            String encryptrequest = new CryptoHandler().encRequest(re,"server cert","clientsignkey");


            System.out.println("encryptrequest" + encryptrequest);
//           \
            response = newFixedLengthResponse(Response.Status.OK, "application/json;charset=utf-8", encryptrequest);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
//            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", " X-Requested-With,Content-Type,accept");


            //return response;
        } else if (session.getUri().contains("/decJson")) {
            JSONObject requestbody = parseParms(session);
            String re = requestbody.toJSONString();
            System.out.println("re" + re);
            String decryptresponse = new CryptoHandler().decResponse(re);


            System.out.println("decryptresponse" + decryptresponse);

            response = newFixedLengthResponse(Response.Status.OK, "application/json;charset=utf-8", decryptresponse);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
//            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", " X-Requested-With,Content-Type,accept");


        } else if (session.getUri().contains("/encForm")) {
//            Map<String, String> parms = new HashMap<>();
//            try {
//                session.parseBody(new HashMap());
//                parms = session.getParms();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ResponseException e) {
//                e.printStackTrace();
//            }
//            try {
//                String encryptrequest = new FormDataEnc().FormDataEnc(parms);
//                response = newFixedLengthResponse(Response.Status.OK, "application/json;charset=utf-8", encryptrequest);
//                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
//                response.addHeader("Access-Control-Allow-Credentials", "true");
//                response.addHeader("Access-Control-Allow-Origin", "*");
//                response.addHeader("Access-Control-Allow-Headers", " X-Requested-With,Content-Type,accept");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


        } else if (session.getUri().contains("/decForm")) {
            JSONObject requestbody = parseParms(session);
            String re = requestbody.toJSONString();
            System.out.println("re" + re);
            String decryptresponse = new JsonDecryptUtils().jsonDecrypt(re);
            System.out.println("decryptresponse" + decryptresponse);

            response = newFixedLengthResponse(Response.Status.OK, "application/json;charset=utf-8", decryptresponse);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
//            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", " X-Requested-With,Content-Type,accept");
        }

//            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("Access-Control-Allow-Headers", "Host,Accept-Language,Accept-Encoding,Access-Control-Request-Method,Access-Control-Request-Headers,Referer,Sec-Fetch-Dest,Sec-Fetch-Mode,Sec-Fetch-Site,content-type,Origin, X-Requested-With, Accept, Connection, User-Agent, Cookie,token");
//            response.addHeader("Access-Control-Allow-Methods", "POST,OPTIONS,GET,HEAD,PUT");
//            response.addHeader("Allow", "POST,OPTIONS,GET,HEAD,PUT");


        return response;
        // return newFixedLengthResponse(Status.NOT_USE_POST, "text/html", "use post");
    }

//        String msg = "<html><body><h1>Hello server</h1>\n";
//        msg += "<p>We serve " + session.getUri() + " !</p>";
//        return newFixedLengthResponse( msg + "</body></html>\n" );


    public JSONObject parseParms(IHTTPSession session) {
        Map<String, String> parms = new HashMap<>();
        try {
            session.parseBody(parms);
            return JSONObject.parseObject(parms.get("postData"));
        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        com.squareup.okhttp.Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}


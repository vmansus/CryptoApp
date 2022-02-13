package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
//import com.example.myapplication.smcipher.FormDataEnc;
import com.example.myapplication.smcipher.FormDataEnc;
import com.example.myapplication.smcipher.JsonDecryptUtils;
import com.example.myapplication.smcipher.JsonUtils;
import com.example.myapplication.smcipher.RawDataDec;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import fi.iki.elonen.NanoHTTPD;
import lombok.SneakyThrows;

public class AndroidWebServer extends NanoHTTPD {



    private final static int PORT = 9000;
    private String configUrl;


    public String getUrl(){
        if(configUrl!=null){
            return this.configUrl;
        }else return "";

    }

    public AndroidWebServer() throws IOException {
        super(PORT);
        start();
//        String url=configUrl;
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
            response = newFixedLengthResponse(Response.Status.OK, "application/json", "success");
            response.addHeader("Access-Control-Allow-Headers", " Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Access-Control-Allow-Methods, Access-Control-Allow-Origin");
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
            this.configUrl=requestbody.get("configUrl").toString();
            saveFileFromUrlWithJavaIO(configUrl);
            requestbody.remove("configUrl");
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
//            System.out.println("11111");
            Map<String, String> parms = new HashMap<>();
            try {
                session.parseBody(new HashMap());
                parms = session.getParms();
                this.configUrl=parms.get("configUrl");
//                MainActivity.url=configUrl;
                System.out.println("configUrl"+configUrl);
                saveFileFromUrlWithJavaIO(configUrl);
                parms.remove("configUrl");
//                Iterator<Map.Entry<String, String>> it = parms.entrySet().iterator();
//                while (it.hasNext()){
//                    Map.Entry<String, String> entry = it.next();
//                    System.out.println(entry.getKey()+"::"+entry.getValue());
//                }





            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            try {
                Map encryptrequestMap = new FormHandler().encRequest(parms,"server cert","clientsignkey");
//                Iterator<Map.Entry<String, String>> it = encryptrequestMap.entrySet().iterator();
//                while (it.hasNext()){
//                    Map.Entry<String, String> entry = it.next();
//                    System.out.println(entry.getKey()+"::"+entry.getValue());
//                }
                String encryptrequest=map2Form((HashMap<String, String>) encryptrequestMap);
                response = newFixedLengthResponse(Response.Status.OK, "application/x-www-form-urlencoded;charset=utf-8", encryptrequest);
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Headers", " Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Access-Control-Allow-Methods, Access-Control-Allow-Origin");
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (session.getUri().contains("/decForm")) {
            Map<String, String> parms = new HashMap<>();
            try {
                session.parseBody(new HashMap());
                parms = session.getParms();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            Map decryptresponseMap=new FormHandler().decResponse(parms);
            String decryptresponse=map2Form((HashMap<String, String>) decryptresponseMap);

            response = newFixedLengthResponse(Response.Status.OK, "application/x-www-form-urlencoded;charset=utf-8", decryptresponse);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
//            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", " Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Access-Control-Allow-Methods, Access-Control-Allow-Origin");
        }else if(session.getUri().contains("/encHtml")){
            Map<String, String> parms = new HashMap<>();
            parms=form2Map(session.getQueryParameterString());
            this.configUrl=parms.get("configUrl");
            System.out.println("configUrl"+configUrl);
            saveFileFromUrlWithJavaIO(configUrl);
            parms.remove("configUrl");


            try {
                Map encryptrequestMap = new FormHandler().encRequest(parms,"server cert","clientsignkey");
                String encryptrequest=map2Form((HashMap<String, String>) encryptrequestMap);
                response = newFixedLengthResponse(Response.Status.OK, "application/x-www-form-urlencoded;charset=utf-8", encryptrequest);
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
                response.addHeader("Access-Control-Allow-Credentials", "true");
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Headers", " Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Access-Control-Allow-Methods, Access-Control-Allow-Origin");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (session.getUri().contains("/decHtml")){
//            String queryParameterString=session.getQueryParameterString();
            Map parms=form2Map(session.getQueryParameterString());
            String encryptedHtml= (String) parms.get("encryptedHtml");
            System.out.println(encryptedHtml);
            String reslut= new HtmlHandler().decHtml(encryptedHtml);
            response = newFixedLengthResponse(Response.Status.OK, "application/x-www-form-urlencoded;charset=utf-8", reslut);
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD,OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", " Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, Access-Control-Allow-Methods, Access-Control-Allow-Origin");

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


    /**
     * hashMap 转化成表单字符串
     *
     * @param map
     * @return
     */
    public static String map2Form(HashMap<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map == null) {
            return stringBuilder.toString();
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
    }
    /**
     * 表单字符串转化成 hashMap
     *
     * @param orderinfo
     * @return
     */
    public static HashMap<String, String> form2Map( String orderinfo) {
        String listinfo[];
        HashMap<String, String> map = new HashMap<String, String>();
        listinfo = orderinfo.split("&");
        for(String s : listinfo)
        {
            String list[]  = s.split("=");
            if(list.length>1)
            {
                map.put(list[0], s.substring(list[0].length()+1,s.length() ));
            }
        }
        return map;
    }





//        public InputStream doInBackground(String urlstr) {
//            System.out.println("urlstr"+urlstr);
//            //We are using inputstream for getting out PDF.
//            InputStream inputStream = null;
//            try {
//                java.net.URL url = new URL(urlstr);
//                System.out.println(url);
//                //Below is the step where we are creating our connection.
//                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//                System.out.println(urlConnection.getResponseCode());
//                if (urlConnection.getResponseCode() == 200) {
//                    //Response is success.
//                    //We are getting input stream from url and storing it in our variable.
//                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                }
//
//
//                try {
//                    File file = new File(Environment.DIRECTORY_DOWNLOADS, "aaa.txt");
//                    try (OutputStream output = new FileOutputStream(file)) {
//                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                        int read;
//
//                        while ((read = inputStream.read(buffer)) != -1) {
//                            output.write(buffer, 0, read);
//                        }
//
//                        output.flush();
//                    }
//                } finally {
//                    inputStream.close();
//                }
//
//
//
//            } catch (IOException e) {
//                //This is the method to handle errors.
//                e.printStackTrace();
//                return null;
//            }
//            return inputStream;
//        }

    public static void saveFileFromUrlWithJavaIO( String fileUrl)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        String[] strs=fileUrl.split("=");
        String filename=strs[1];
        try {
            in = new BufferedInputStream(new URL(fileUrl).openStream());
//            File aatxt = new File(Environment.DIRECTORY_DOWNLOADS, "aaa.txt");

//            File file = new File(Environment.DIRECTORY_DOWNLOADS, "aaa.txt");
            java.io.File file = new java.io.File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/"+filename);


            fout = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null)
                in.close();
            if (fout != null)
                fout.close();
        }
    }



//    File outputFile = new File(getDownloadPath(mContext, fileName));
//        outputFile.getAbsolutePath();
//
//    public static String getDownloadPath(Context context, String fileName) {
//        return getDownloadPath(context) + File.separator + fileName;
//    }
//
//    public static String getDownloadPath(Context context) {
//        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//    }


}


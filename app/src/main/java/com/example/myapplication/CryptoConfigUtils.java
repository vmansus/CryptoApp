package com.example.myapplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CryptoConfigUtils {

    public int getWorkMode(Properties properties) throws IOException {
        int mode= Integer.parseInt(properties.getProperty("workmode"));
        return mode;
    }

    public int getIsWhole(Properties properties) throws IOException {
        int mode= Integer.parseInt(properties.getProperty("isWhole"));
        return mode;
    }

    public int getEncAlg(Properties properties) throws IOException {
        int mode= Integer.parseInt(properties.getProperty("encAlg"));
        return mode;
    }

    public int getHtmlMode(Properties properties) throws IOException {
        int mode= Integer.parseInt(properties.getProperty("htmlmode"));
        return mode;
    }

    public List<String> getRequestEncParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("requestEnc");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getRequestSignParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("requestSign");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getResponseEncParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("responseEnc");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getResponseSignParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("responseSign");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getFormRequestEncParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("formrequestEnc");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getFormRequestSignParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("formrequestSign");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getFormResponseEncParms(Properties properties) throws IOException {

        String requestEnc=properties.getProperty("formresponseEnc");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }

    public List<String> getFormResponseSignParms(Properties properties) throws IOException {
//        Properties properties=getProperties();
        String requestEnc=properties.getProperty("formresponseSign");
        String[] strings=requestEnc.split("\\s+");
        List<String> stringList=new ArrayList<>();
        for (String s:strings){
//            String m=s.substring(0,1).toUpperCase()+s.substring(1);
            stringList.add(s);
        }
        return stringList;
    }
}

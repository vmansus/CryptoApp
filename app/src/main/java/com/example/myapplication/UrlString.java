package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.UrlConfig;

/**
 * 网络请求
 */

public class UrlString {

    private static final String IP = "192.168.1.10:8000";

    private String contrastIPName = "contrastIP";

    // 上传路径
    private String ip;
    private String ipAddress;

    public void setIPAddress(Context context) {
        //Properties proper = ProperTies.getProperties(context);
        //this.ip = proper.getProperty(contrastIPName, "");
        SharedPreferences proper = UrlConfig.getProperties(context);
        this.ip = proper.getString(contrastIPName, "");
        // 设置默认值
        if (this.ip.equals("")){
            this.ip = IP;
        }
        this.ipAddress = "http://" + this.ip + "/v1.0/index.html";
    }

    public String setIPAddress(Context context, String keyValue) {
        // String result = ProperTies.setProperties(context, contrastIPName, keyValue);
        String result = UrlConfig.setProperties(context, contrastIPName, keyValue);
        this.ip = keyValue;
        this.ipAddress = "http://" + this.ip + "/v1.0/index.html";
        return result;
    }

    public String getIP() {
        return this.ip;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }
}
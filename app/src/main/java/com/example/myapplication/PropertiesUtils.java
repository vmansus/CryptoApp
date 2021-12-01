package com.example.myapplication;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    //1、配置文件的位置在assets资源目录下
    private final static String m_strPath = "appConfig.properties";
    //2、配置文件的位置在源代码根目录(src下)
    //private final static String m_strPath = "/global.properties";

    public static Properties getProperties(Context c) {
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context获取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            //InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
//            InputStream in = PropertiesUtils.class.getResourceAsStream(m_strPath);
            props.load(c.getAssets().open("appConfig.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * 使用样例
     */
    private void example() {
        //String url = PropertiesUtils.getProperties(getApplicationContext()).getProperty("url");
        String url = PropertiesUtils.getProperties(null).getProperty("url");
    }


}
 


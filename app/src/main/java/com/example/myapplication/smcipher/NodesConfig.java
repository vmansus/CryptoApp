package com.example.myapplication.smcipher;

import com.example.myapplication.MyApplication;
import com.example.myapplication.PropertiesUtils;
import com.google.common.collect.Lists;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//@Component
//@PropertySource(value = {"classpath:application-crypto.yml"}, factory = YamlPropertySourceFactory.class)
//@ConfigurationProperties(prefix = "nodeinfo.my")
//@Configuration
//@PropertySource(value = "classpath:application.yml", encoding = "UTF-8", factory = YamlPropertyLoaderFactory.class)
//@ConfigurationProperties(prefix = "node")
//@Data

//@Component
//@PropertySource("classpath:application.properties")
//@PropertySource("classpath:application.properties")
//@ConfigurationProperties(prefix = "node.info")
@Data
//@Primary
public class NodesConfig {
//    Properties props=new Properties();
//    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
//    props.load(inputStream);

     Properties pro =  PropertiesUtils.getProperties(MyApplication.getContext());
     List<String> encryptlist= Lists.newArrayList(pro.getProperty("node.info.encryptlist[0]"),pro.getProperty("node.info.encryptlist[1]"),pro.getProperty("node.info.encryptlist[2]"),pro.getProperty("node.info.encryptlist[3]"));
     List<String> signlist = Lists.newArrayList(pro.getProperty("node.info.signlist[0]"),pro.getProperty("node.info.signlist[1]"),pro.getProperty("node.info.signlist[2]"));
     List<String> reenc=Lists.newArrayList(pro.getProperty("node.info.reenc[0]"),pro.getProperty("node.info.reenc[1]")) ;
     List<String> resign=Lists.newArrayList(pro.getProperty("node.info.resign[0]"),pro.getProperty("node.info.resign[1]")) ;




     List<String> formenc =Lists.newArrayList(pro.getProperty("node.info.formenc[0]"),pro.getProperty("node.info.formenc[1]"),pro.getProperty("node.info.formenc[2]"));
     List<String> formsign =Lists.newArrayList(pro.getProperty("node.info.formsign[0]"),pro.getProperty("node.info.formsign[1]"),pro.getProperty("node.info.formsign[2]"));
     List<String> formreenc =Lists.newArrayList(pro.getProperty("node.info.formreenc[0]"),pro.getProperty("node.info.formreenc[1]"));
     List<String> formresign =Lists.newArrayList(pro.getProperty("node.info.formresign[0]"),pro.getProperty("node.info.formresign[1]"));


//
//     List<String> encryptlist= Lists.newArrayList("name","sex","customerInfo.date","cardInfo");
//     List<String> signlist = Lists.newArrayList("name","sex","customerInfo");
//     List<String> reenc=Lists.newArrayList("username","cardnum") ;
//     List<String> resign =Lists.newArrayList("username","cardnum");
//
//     List<String> formenc =Lists.newArrayList("name","cashCardNo","mobile");
//     List<String> formsign =Lists.newArrayList("name","cashCardNo","mobile");
//     List<String> formreenc =Lists.newArrayList("username","cardnum");
//     List<String> formresign =Lists.newArrayList("username","cardnum");
}

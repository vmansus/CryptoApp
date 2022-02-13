package com.example.myapplication;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsoupConfig {
    public List<String> slectEncElements(org.dom4j.Document config) throws DocumentException {
//        String html, org.dom4j.Document config
        //创建SAXReader
        List<String> resElements=new ArrayList<>();
//        SAXReader sax = new SAXReader();
//        Reader reader = new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<body>\n" +
//                "    <encrypt>\n" +
//                "        <id>\n" +
//                "            <td>1</td>\n" +
//                "            <td>2</td>\n" +
//                "<!--            <td></td>-->\n" +
//                "        </id>\n" +
//                "        <class>\n" +
//                "            <td></td>\n" +
//                "            <td></td>\n" +
//                "            <td></td>\n" +
//                "        </class>\n" +
//                "    </encrypt>\n" +
//                "    <sign>\n" +
//                "        <id>\n" +
//                "            <td>1</td>\n" +
//                "            <td>2</td>\n" +
//                "<!--            <td></td>-->\n" +
//                "        </id>\n" +
//                "        <class>\n" +
//                "            <td></td>\n" +
//                "            <td></td>\n" +
//                "            <td></td>\n" +
//                "        </class>\n" +
//                "    </sign>\n" +
//                "</body>");
//        Document doc= sax.read(reader);
        //获得根元素
//        org.jsoup.nodes.Document htmlDoc=Jsoup.parse(html);
        Element root = config.getRootElement();

        //获取encrypt元素
        Element child1 = root.element("encrypt");
        //获得encrypt全部子元素
        List<Element> elements = child1.elements();
        for (int i=0;i<elements.size();i++){
            Element e=elements.get(i);
            Iterator iterator=e.elementIterator();
            while (iterator.hasNext()){
                Element entry= (Element) iterator.next();
                if(entry.getData()!=null&&entry.getData()!=""){
//                    System.out.println(entry.getName());
                    resElements.add(cssQueryBuilder(e.getName(),entry.getName(),entry.getData().toString()));
                }
//                System.out.println(e.getName()+"::"+entry.getData());
            }
        }

//        //获得text值：获得第一个stu的age
//        Element child2 = root.element("age");
//        System.out.println(child2.getText());
//
//        //获得text值：获得第二个stu的name
//        Element child3 = (Element) root.elements().get(1);
//        System.out.println(child3.element("name").getText());
//        org.jsoup.nodes.Document document= Jsoup.parse(html);
        return resElements;

//        document.select().


    }

    public List<String> slectSigElements(org.dom4j.Document config) throws DocumentException {
        List<String> resElements=new ArrayList<>();
//        org.jsoup.nodes.Document htmlDoc=Jsoup.parse(html);
        Element root = config.getRootElement();

        //获取encrypt元素
        Element child1 = root.element("sign");
        //获得encrypt全部子元素
        List<Element> elements = child1.elements();
        for (int i=0;i<elements.size();i++){
            Element e=elements.get(i);
            Iterator iterator=e.elementIterator();
            while (iterator.hasNext()){
                Element entry= (Element) iterator.next();
                if(entry.getData()!=null&&entry.getData()!=""){
                    resElements.add(cssQueryBuilder(e.getName(),entry.getName(),entry.getData().toString()));
                }
            }
        }
        return resElements;
    }



//    public Elements slectSigElements(String html, Document config){
//        org.jsoup.nodes.Document document= Jsoup.parse(html);
//        document.select()
//
//
//    }



    static String cssQueryBuilder(String ename,String entryName,String key){
//        return "div[id=Certs]";
        String a=entryName+"["+ename+"="+key+"]";
        return a;
    }

}

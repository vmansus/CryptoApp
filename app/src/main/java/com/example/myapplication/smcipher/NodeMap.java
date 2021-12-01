package com.example.myapplication.smcipher;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableConfigurationProperties(NodesConfig.class)
@Component
public class NodeMap {


//    @Autowired
//    private NodesConfig NodesConfig;
   // private NodesConfig nodesConfig = BeanUtils.getBean(NodesConfig.class);

    public Map<String, List<String>> encryptNodeMap(){
        List<String> lists=new NodesConfig().getEncryptlist();
        Map<String, List<String>> Map = new HashMap<String, List<String>>(){
            {
                put("defult",  lists);
            }
        };
        return Map;
    }

    public Map<String, List<String>> reencNodeMap(){
        List<String> lists=new NodesConfig().getReenc();
        Map<String, List<String>> Map = new HashMap<String, List<String>>(){
            {
                put("defult",  lists);
            }
        };
        return Map;
    }


    public Map<String, List<String>> signNodeMap(){
        List<String> lists=new NodesConfig().getSignlist();
        Map<String, List<String>> Map = new HashMap<String, List<String>>(){
            {
                put("defult",  lists);
            }
        };
        return Map;
    }

    public Map<String, List<String>> resignNodeMap(){
        List<String> lists=new NodesConfig().getResign();
        Map<String, List<String>> Map = new HashMap<String, List<String>>(){
            {
                put("defult",  lists);
            }
        };
        return Map;
    }

    public List<String> signNodelist(){
        List<String> lists=new NodesConfig().getSignlist();
        return lists;
    }
    public List<String> resignlist(){
        List<String> lists=new NodesConfig().getReenc();
        return lists;
    }

    public List<String> formenc(){
        List<String> lists=new NodesConfig().getFormenc();
        return lists;
    }

    public List<String> formsign(){
        List<String> lists=new NodesConfig().getFormsign();
        return lists;
    }

    public List<String> formreenc(){
        List<String> lists=new NodesConfig().getFormreenc();
        return lists;
    }

    public List<String> formresign(){
        List<String> lists=new NodesConfig().getFormresign();
        return lists;
    }

}
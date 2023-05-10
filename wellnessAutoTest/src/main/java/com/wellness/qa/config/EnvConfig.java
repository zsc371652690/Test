package com.wellness.qa.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    public static Map<String, String> ENV = new HashMap<>();
    public static Object LOCK_OBJECT;


    static{
        LOCK_OBJECT = new Object();
        String prof=System.getProperty("test.env","default") ;
        String configFileName = "env-"+prof+".properties";
        logger.info(configFileName);
        InputStream is = null;
        try {
            File file = new File("src/main/resources/"+configFileName);
            is = new FileInputStream(file);
            Properties properties = new Properties();
            if(is!=null){
                properties.load(is);
                for(Map.Entry<Object,Object> entry: properties.entrySet()){
                    ENV.put(entry.getKey().toString(),entry.getValue().toString());
                }
                is.close();
            }else{
                System.out.println(String.format("Cannot find %s properties file",configFileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String gBaseUrl="https://g.1012china.com";
        String adminBaseUrl="https://admin.1012wellness.com";
        String checkInBaseUrl="https://checkin-fat.1012wellness.com";
        if(!"pro".equals(prof.toLowerCase()) && !"default".equals(prof.toLowerCase())){
            gBaseUrl="https://g-"+prof+".1012china.com";
            adminBaseUrl="https://admin-"+prof+".1012wellness.com";
            checkInBaseUrl="https://checkin-"+prof+".1012wellness.com";
        }else if("default".equals(prof.toLowerCase())){
            gBaseUrl="https://g-fat.1012china.com";
            adminBaseUrl="https://admin-fat.1012wellness.com";
            checkInBaseUrl="https://checkin-fat.1012wellness.com";
        }
        ENV.put("gBaseUrl",gBaseUrl);
        ENV.put("adminBaseUrl",adminBaseUrl);
        ENV.put("checkInBaseUrl",checkInBaseUrl);

    }

}


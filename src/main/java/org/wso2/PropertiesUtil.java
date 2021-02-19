package org.wso2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    static String readProperty(String key) {
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.properties";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }
}

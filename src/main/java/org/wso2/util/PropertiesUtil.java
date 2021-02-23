package org.wso2.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to read the properties file.
 */
public class PropertiesUtil {
    public static String readProperty(String key) {
        Properties prop = new Properties();
        String fileName = "src/main/resources/config.properties";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            prop.load(is);
        } catch (Exception e) {
            Logger.getLogger(PropertiesUtil.class.getName()).log(Level.SEVERE, "Property couldn't be read.");
        }
        return prop.getProperty(key);
    }
}

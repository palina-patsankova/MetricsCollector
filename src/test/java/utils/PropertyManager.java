package utils;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Log4j2
public class PropertyManager {

    private final String propertyFilePath;
    private Properties prop;

    public PropertyManager() {
        propertyFilePath = System.getProperty("user.dir") + "/src/test/resources/test.properties";
        loadData();
    }

    public String getProperty(String propertyName) {
        String sysProp = System.getProperty(propertyName);
        if (sysProp == null || sysProp.isEmpty()) {
            return get(propertyName);
        } else {
            return sysProp;
        }
    }

    private void loadData() {
        prop = new Properties();
        try {
            prop.load(new FileInputStream(propertyFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String propertyName) {
        return prop.getProperty(propertyName);
    }
}
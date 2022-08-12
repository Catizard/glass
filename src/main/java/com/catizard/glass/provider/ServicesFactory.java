package com.catizard.glass.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServicesFactory {
//    private static Properties properties;
    private static final Map<String, Object> map = new ConcurrentHashMap<>();
    
//    static {
//        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
//            properties = new Properties();
//            properties.load(in);
//            Set<String> names = properties.stringPropertyNames();
//            for (String name : names) {
//                Class<?> instanceClass = Class.forName(properties.getProperty(name));
//                map.put(name, instanceClass.newInstance());
//            }
//        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
    
    public static <T> T getServices(String serviceName) {
        return (T) map.get(serviceName);
    }
    public static void setService(String serviceName, Object serviceInstance) {
        map.put(serviceName ,serviceInstance);
    }
}

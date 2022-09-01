package com.catizard.glass.service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@RPCService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String message) {
        return "say hello " + message;
    }
    
    private static void doFile(File file, List<String> classPaths) {
        if (file.isDirectory()) {
            File[] chf = file.listFiles();
            for (File f : chf) {
                doFile(f, classPaths);
            }
        } else if (file.getName().endsWith(".class")) {
            classPaths.add(file.getPath());
        }
    }
    public static void main(String[] args) throws ClassNotFoundException {
        List<String> classPaths = new ArrayList<>();
        String basepack = "com.catizard.glass.service".replace(".", File.separator);
        String classpath = HelloServiceImpl.class.getResource("/").getPath();
        String targetPath = classpath + basepack;
        File file = new File(targetPath);
        doFile(file, classPaths);
        for (String path : classPaths) {
            String className = path.replace(classpath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\",".").replace(".class", "");
            Class<?> clz = Class.forName(className);
            RPCService rpcService = clz.getAnnotation(RPCService.class);
            if (rpcService != null) {
                System.out.println(rpcService.value());
            }
            
            if (clz.isInterface()) {
                System.out.println(clz.getName() + " is interface");    
            } else {
                System.out.println(clz.getName() + " is class");
            }
        }
    }
}

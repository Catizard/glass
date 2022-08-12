package com.catizard.glass.service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@RPCService("HelloService")
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
        System.out.println(targetPath);
        File file = new File(targetPath);
        System.out.println(file.getPath());
        doFile(file, classPaths);
        System.out.println(classPaths);
        for (String path : classPaths) {
            String className = path.replace(classpath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\",".").replace(".class", "");
            System.out.println(className);
            Class<?> clz = Class.forName(className);
            RPCService rpcService = clz.getAnnotation(RPCService.class);
            if (rpcService != null) {
                System.out.println(rpcService.value());
            }
        }
    }
}

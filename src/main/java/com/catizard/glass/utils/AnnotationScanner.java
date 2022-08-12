package com.catizard.glass.utils;

import com.catizard.glass.service.RPCService;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationScanner {
    private static <A extends Annotation> void doFile(File file, Class<A> targetClass, List<Class<?>> annotationList, String classpath) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File ch : files) {
                doFile(ch, targetClass, annotationList, classpath);
            }
        } else if (file.getName().endsWith(".class")) {
            String className = file.getPath().replace(classpath.replace("/", "\\").replaceFirst("\\\\", ""), "")
                    .replace("\\", ".").
                    replace(".class", "");
            try {
                Class<?> clz = Class.forName(className);
                if (clz.getAnnotation(targetClass) != null) {
                    annotationList.add(clz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static <A extends Annotation> List<Class<?>> scan(String packName, Class<A> targetClass) {
        List<Class<?>> classList = new ArrayList<>();
        packName = packName.replace(".", File.separator);
        String classpath = Objects.requireNonNull(AnnotationScanner.class.getResource("/")).getPath();
        File file = new File(classpath + packName);
        doFile(file, targetClass, classList, classpath);
        return classList;
    }

    public static void main(String[] args) {
        List<Class<?>> classList = scan("com.catizard.glass.service", RPCService.class);
        System.out.println(classList);
    }
}

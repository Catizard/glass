package com.catizard.ptp.services;

import java.lang.annotation.Annotation;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String message) {
        return "Hello " + message;
    }


}

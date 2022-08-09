package com.catizard.glass.service;

import java.lang.annotation.Annotation;

@RPCService("HelloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String message) {
        return "say hello " + message;
    }

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RPCService rpcService = HelloServiceImpl.class.getAnnotation(RPCService.class);
        System.out.println(rpcService.value());
        rpcService = HelloService.class.getAnnotation(RPCService.class);
        System.out.println(rpcService.value());
    }
}

package com.catizard.glass.service;


@RPCService("HelloService")
public interface HelloService {
    public String sayHello(String message);
}

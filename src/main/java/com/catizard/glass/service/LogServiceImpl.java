package com.catizard.glass.service;

@RPCService("LogService")
public class LogServiceImpl implements LogService {
    //this class is only for testing AnnotationScanner
    @Override
    public void log() {
        System.out.println("log...");
    }
}

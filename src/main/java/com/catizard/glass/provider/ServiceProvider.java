package com.catizard.glass.provider;

import com.catizard.glass.utils.InetAddress;

public class ServiceProvider {
    //TODO ServiceProvider should automatically scan all the services and register them
    private InetAddress centerAddress;
    private ServiceRegister register;
    
    public ServiceProvider(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
        this.register = new ServiceRegister(centerAddress);
    }
    
    public void registerService(String serviceName, InetAddress serviceAddress) {
        register.sendRegisterMessage(serviceName, serviceAddress);
    }

    public static void main(String[] args) {
        ServiceProvider serviceProvider = new ServiceProvider(new InetAddress("localhost", 8080));
        serviceProvider.registerService("HelloService", new InetAddress("localhost", 9090));
    }
}

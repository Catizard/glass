package com.catizard.glass.message;

import com.catizard.glass.utils.InetAddress;

public class RegisterServiceRequestMessage extends Message {
    private String serviceName;
    private InetAddress serviceAddress;
    
    public RegisterServiceRequestMessage() {
    }

    public RegisterServiceRequestMessage(String serviceName, String inetHost, int inetPort) {
        this.serviceName = serviceName;
        this.serviceAddress = new InetAddress(inetHost, inetPort);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public InetAddress getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(InetAddress serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
    
    @Override
    public String toString() {
        return "ServiceRegisterMessage{" +
                "serviceName='" + serviceName + '\'' +
                ", ipaddress=" + serviceAddress +
                '}';
    }

    @Override
    public int getMessageType() {
        return RegisterServiceRequestMessage;
    }
}

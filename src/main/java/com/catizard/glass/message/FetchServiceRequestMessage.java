package com.catizard.glass.message;

public class FetchServiceRequestMessage extends Message {
    private String serviceName;
    
    public FetchServiceRequestMessage() {
        
    }
    
    public FetchServiceRequestMessage(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "FetchServiceRequestMessage{" +
                "serviceName='" + serviceName + '\'' +
                '}';
    }

    @Override
    public int getMessageType() {
        return FetchServiceRequestMessage;
    }
}

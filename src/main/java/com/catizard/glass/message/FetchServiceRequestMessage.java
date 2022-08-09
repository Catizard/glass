package com.catizard.glass.message;

import com.catizard.glass.utils.RequestIdentify;

public class FetchServiceRequestMessage extends Message {
    private String serviceName;
    private RequestIdentify id;
    
    public FetchServiceRequestMessage() {
        
    }
    
    public FetchServiceRequestMessage(String serviceName, RequestIdentify id) {
        this.serviceName = serviceName;
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public RequestIdentify getId() {
        return id;
    }

    public void setId(RequestIdentify id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FetchServiceRequestMessage{" +
                "serviceName='" + serviceName + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public int getMessageType() {
        return FetchServiceRequestMessage;
    }
}

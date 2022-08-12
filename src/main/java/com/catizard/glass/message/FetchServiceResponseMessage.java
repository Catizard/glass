package com.catizard.glass.message;

import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.RequestIdentify;

import java.util.List;

public class FetchServiceResponseMessage extends Message {
    private List<InetAddress> addressList;
    private RequestIdentify id;
    private Exception exceptionValue;

    public FetchServiceResponseMessage() {
    }

    public List<InetAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<InetAddress> addressList) {
        this.addressList = addressList;
    }

    public RequestIdentify getId() {
        return id;
    }

    public void setId(RequestIdentify id) {
        this.id = id;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return FetchServiceResponseMessage;
    }

    @Override
    public String toString() {
        return "FetchServiceResponseMessage{" +
                "addressList=" + addressList +
                ", id=" + id +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}

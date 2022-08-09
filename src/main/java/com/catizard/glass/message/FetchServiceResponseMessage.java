package com.catizard.glass.message;

import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.RequestIdentify;

public class FetchServiceResponseMessage extends Message {
    private InetAddress address;
    private RequestIdentify id;
    private Exception exceptionValue;

    public FetchServiceResponseMessage() {
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
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
                "address=" + address +
                ", id=" + id +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}

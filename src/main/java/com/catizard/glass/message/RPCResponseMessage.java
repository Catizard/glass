package com.catizard.glass.message;

import com.catizard.glass.utils.RequestIdentify;

public class RPCResponseMessage extends Message {
    private RequestIdentify id;
    private Object returnValue;
    private Exception exceptionValue;

    public RequestIdentify getId() {
        return id;
    }

    public void setId(RequestIdentify id) {
        this.id = id;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return RPCResponseMessage;
    }

    @Override
    public String toString() {
        return "RPCResponseMessage{" +
                "id=" + id +
                ", returnValue=" + returnValue +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}

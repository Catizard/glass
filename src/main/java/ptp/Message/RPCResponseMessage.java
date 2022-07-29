package ptp.Message;

import ptp.RequestIdentify;

public class RPCResponseMessage extends Message {
    private Object returnValue;
    private Exception exceptionValue;
    
    private RequestIdentify requestIdentify;

    public Object getReturnValue() {
        return returnValue;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }
    
    @Override
    public int getMessageType() {
        return RPCResponseMessage;
    }

    public RequestIdentify getRequestIdentify() {
        return requestIdentify;
    }

    public void setRequestIdentify(RequestIdentify requestIdentify) {
        this.requestIdentify = requestIdentify;
    }

    @Override
    public String toString() {
        return "RPCResponseMessage{" +
                "returnValue=" + returnValue +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}

package com.catizard.ptp.Message;

import com.catizard.ptp.RequestIdentify;

import java.util.Arrays;

public class RPCRequestMessage extends Message {
    private RequestIdentify requestIdentify;
    private String interfaceName;
    private String methodName;
    private Class<?> returnType;
    private Class[] parameterTypes;
    private Object[] parameterValues;

    public RPCRequestMessage() {
    }

    public RPCRequestMessage(int clientID, int requestID, String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValues) {
        requestIdentify = new RequestIdentify(clientID, requestID);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    public RequestIdentify getRequestIdentify() {
        return requestIdentify;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }
        
    @Override
    public int getMessageType() {
        return RPCRequestMessage;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public String toString() {
        return "RPCRequestMessage{" +
                "ClientID=" + requestIdentify.ClientID +
                ", RequestID=" + requestIdentify.RequestID +
                ", interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameterValues=" + Arrays.toString(parameterValues) +
                '}';
    }
}

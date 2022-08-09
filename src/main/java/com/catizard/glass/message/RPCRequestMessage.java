package com.catizard.glass.message;

import com.catizard.glass.utils.RequestIdentify;

import java.util.Arrays;

public class RPCRequestMessage extends Message {
    private RequestIdentify id;
    private String serviceName;
    private String methodName;
    private Class<?> returnType;
    private Class<?>[] parameterTypes;
    private Object[] parameterValues;

    public RPCRequestMessage() {
    }

    public RPCRequestMessage(RequestIdentify id, String serviceName, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object[] parameterValues) {
        this.id = id;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    public RequestIdentify getId() {
        return id;
    }

    public void setId(RequestIdentify id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public int getMessageType() {
        return RPCRequestMessage;
    }

    @Override
    public String toString() {
        return "RPCRequestMessage{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameterValues=" + Arrays.toString(parameterValues) +
                '}';
    }
}

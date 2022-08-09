package com.catizard.glass.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Message implements Serializable {
    private int messageType;
    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();
    public int getMessageType() {
        return messageType;
    }
    
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }
    
    public static final int ServiceRegisterRequestMessage = 0;
    
    static {
        messageClasses.put(ServiceRegisterRequestMessage, ServiceRegisterRequestMessage.class);
    }
}

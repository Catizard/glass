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
    
    public static final int RegisterServiceRequestMessage = 0;
    public static final int FetchServiceRequestMessage = 1;
    public static final int FetchServiceResponseMessage = 2;
    
    static {
        messageClasses.put(RegisterServiceRequestMessage, com.catizard.glass.message.RegisterServiceRequestMessage.class);
        messageClasses.put(FetchServiceRequestMessage, com.catizard.glass.message.FetchServiceRequestMessage.class);
        messageClasses.put(FetchServiceResponseMessage, com.catizard.glass.message.FetchServiceResponseMessage.class);
    }
}

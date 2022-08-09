package com.catizard.ptp.Message;

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
    
    public static final int RPCRequestMessage = 0;
    public static final int RPCResponseMessage = 1;
    
    static {
        messageClasses.put(RPCRequestMessage, com.catizard.ptp.Message.RPCRequestMessage.class);
        messageClasses.put(RPCResponseMessage, com.catizard.ptp.Message.RPCResponseMessage.class);
    }
    
}

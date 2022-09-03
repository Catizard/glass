package com.catizard.glass.network.utils.message;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMessage implements Message {
    private final int messageType;
    private final int requestId;
    public AbstractMessage(int messageType, int requestId) {
        this.messageType = messageType;
        this.requestId = requestId;
    }
    public static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();
    public int getMessageType() {
        return messageType;
    }
    public int getRequestId() {
        return requestId;
    }
}

package com.catizard.glass.message;

import com.catizard.glass.utils.RequestIdentify;

public class HeartbeatRequestMessage extends Message {
    private RequestIdentify id;

    public RequestIdentify getId() {
        return id;
    }

    public void setId(RequestIdentify id) {
        this.id = id;
    }

    public HeartbeatRequestMessage() {
    }

    public HeartbeatRequestMessage(RequestIdentify id) {
        this.id = id;
    }

    @Override
    public int getMessageType() {
        return HeartbeatRequestMessage;
    }
}

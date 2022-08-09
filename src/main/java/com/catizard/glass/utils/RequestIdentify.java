package com.catizard.glass.utils;

public final class RequestIdentify {
    public final int clientId;
    public final int requestId;

    private RequestIdentify() {
        clientId = 0;
        requestId = 0;
    }
    public RequestIdentify(int clientID, int requestId) {
        clientId = clientID;
        this.requestId = requestId;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RequestIdentify)) {
            return false;
        }

        return clientId == ((RequestIdentify) obj).clientId && requestId == ((RequestIdentify) obj).requestId;
    }

    @Override
    public int hashCode() {
        //TODO this hashCode() is plain and I dont give a confidence whether it will fail some case
        long sum = clientId + requestId;
        return (int) (sum % Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return "RequestIdentify{" +
                "ClientID=" + clientId +
                ", RequestID=" + requestId +
                '}';
    }
}
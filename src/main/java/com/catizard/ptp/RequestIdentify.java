package com.catizard.ptp;

public final class RequestIdentify {
    public final int ClientID;
    public final int RequestID;
    
    private RequestIdentify() {
        ClientID = 0;
        RequestID = 0;
    }
    public RequestIdentify(int clientID, int requestID) {
        ClientID = clientID;
        RequestID = requestID;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RequestIdentify)) {
            return false;
        }

        return ClientID == ((RequestIdentify) obj).ClientID && RequestID == ((RequestIdentify) obj).RequestID;
    }

    @Override
    public int hashCode() {
        //TODO this hashCode() is plain and I dont give a confidence whether it will fail some case
        long sum = ClientID + RequestID;
        return (int) (sum % Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return "RequestIdentify{" +
                "ClientID=" + ClientID +
                ", RequestID=" + RequestID +
                '}';
    }
}

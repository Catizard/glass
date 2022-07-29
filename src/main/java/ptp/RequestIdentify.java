package ptp;

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
}

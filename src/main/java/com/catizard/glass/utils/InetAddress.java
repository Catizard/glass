package com.catizard.glass.utils;

public class InetAddress {
    private String inetHost;
    private int inetPort;

    public InetAddress() {
    }

    public InetAddress(String inetHost, int inetPort) {
        this.inetHost = inetHost;
        this.inetPort = inetPort;
    }

    public String getInetHost() {
        return inetHost;
    }

    public int getInetPort() {
        return inetPort;
    }

    public void setInetHost(String inetHost) {
        this.inetHost = inetHost;
    }

    public void setInetPort(int inetPort) {
        this.inetPort = inetPort;
    }

    @Override
    public String toString() {
        return "InetAddress{" +
                "inetHost='" + inetHost + '\'' +
                ", inetPort=" + inetPort +
                '}';
    }
}

package com.catizard.glass.grsc.interfaces;

import com.catizard.glass.utils.InetAddress;

import java.util.ArrayList;

public interface ServicesCollection {
    boolean addService(String serviceName, InetAddress address);
    boolean removeService(String serviceName, InetAddress address);
    ArrayList<InetAddress> getServices(String serviceName);
}

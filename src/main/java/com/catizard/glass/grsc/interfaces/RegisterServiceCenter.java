package com.catizard.glass.grsc.interfaces;

import com.catizard.glass.network.server.interfaces.Server;
import com.catizard.glass.utils.InetAddress;

import java.util.ArrayList;
import java.util.List;

public interface RegisterServiceCenter extends Server {
    void submit(String serviceName, InetAddress address);
    List<InetAddress> fetch(String serviceName);
    void remove(String serviceName, InetAddress address);
}

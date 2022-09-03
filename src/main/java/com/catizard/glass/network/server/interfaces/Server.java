package com.catizard.glass.network.server.interfaces;

import com.catizard.glass.utils.InetAddress;

public interface Server {
    public void listenTo(InetAddress ip);
}

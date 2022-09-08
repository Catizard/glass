package com.catizard.glass.network.server;

import com.catizard.glass.network.server.abstracts.NettyServer;
import com.catizard.glass.utils.InetAddress;

public class DefaultRunnableNettyServer extends NettyServer implements Runnable {
    @Override
    public void run() {
        setupServer();
    }
}

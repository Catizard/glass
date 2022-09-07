package com.catizard.glass.network.server;

import com.catizard.glass.network.server.abstracts.NettyServer;
import com.catizard.glass.utils.InetAddress;

public class DefaultRunnableNettyServer extends NettyServer implements Runnable {
    private Thread thread;
    @Override
    public void run() {
        setupServer();
    }

    @Override
    public void listenTo(int port) {
        super.listenTo(port);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void listenTo(InetAddress ip) {
        super.listenTo(ip);
        thread = new Thread(this);
        thread.start();
    }
}

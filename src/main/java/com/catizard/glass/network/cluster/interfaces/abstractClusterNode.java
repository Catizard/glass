package com.catizard.glass.network.cluster.interfaces;

import com.catizard.glass.network.client.interfaces.Client;
import com.catizard.glass.network.client.server.interfaces.Server;
import com.catizard.glass.network.utils.message.Message;
import com.catizard.glass.utils.InetAddress;
import io.netty.util.concurrent.Promise;

public abstract class abstractClusterNode implements ClusterNode {
    private final Client client;
    private final Server server;
    
    public abstractClusterNode(Client client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void connectTo(InetAddress ip) throws Exception {
        client.connectTo(ip);
    }

    @Override
    public Object call(Message param) throws Exception {
        return client.call(param);
    }

    @Override
    public Object call(InetAddress ip, Message param) throws Exception {
        return client.call(ip, param);
    }

    @Override
    public Promise<Object> callAsync(Message param) throws Exception {
        return client.callAsync(param);
    }

    @Override
    public Promise<Object> callAsync(InetAddress ip, Message param) throws Exception {
        return client.callAsync(ip, param);
    }

    @Override
    public void listenTo(InetAddress ip) {
        server.listenTo(ip);
    }
}

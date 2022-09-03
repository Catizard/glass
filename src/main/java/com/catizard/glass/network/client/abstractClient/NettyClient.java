package com.catizard.glass.network.client.abstractClient;

import com.catizard.glass.network.client.interfaces.Client;
import com.catizard.glass.network.utils.message.Message;
import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

public abstract class NettyClient implements Client {
    private Bootstrap bootstrap;
    private Channel channel;

    @Override
    public void connectTo(InetAddress ip) throws Exception {
        channel = bootstrap.connect(ip.getInetHost(), ip.getInetPort()).sync().channel();
    }

    public void beforeSetup() {
        
    }
    public void afterSetup(Bootstrap bootstrap) {
        
    }
    public void setupClient() {
        beforeSetup();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        afterSetup(bootstrap);
    }

    @Override
    public Object call(Message param) throws Exception {
        Promise<Object> promise = callAsync(param);
        promise.await();
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new RuntimeException(promise.cause());
        }
    }

    @Override
    public Object call(InetAddress ip, Message param) throws Exception {
        //refresh the channel
        connectTo(ip);
        return call(param);
    }

    @Override
    public Promise<Object> callAsync(Message param) throws Exception {
        return doCallAsync(param);
    }

    public abstract Promise<Object> doCallAsync(Message param);

    @Override
    public Promise<Object> callAsync(InetAddress ip, Message param) throws Exception {
        connectTo(ip);
        return callAsync(param);
    }
}

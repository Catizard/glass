package com.catizard.glass.network.client.abstractClient;

import com.catizard.glass.network.client.interfaces.Client;
import com.catizard.glass.network.utils.message.Message;
import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class NettyClient implements Client {
    private Bootstrap bootstrap;
    private Channel channel;
    private InetAddress cachedAddress;

    public void configureChannelType(Class<? extends Channel> clz) {
        bootstrap.channel(clz);
    }
    
    public void configureChannelHandler(ChannelInitializer<? extends Channel> initializer) {
        bootstrap.handler(initializer);
    }
    
    public void setupClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
    }

    @Override
    public final void connectTo(InetAddress ip) throws Exception {
        if(ip != null) {
            if (!ip.equals(cachedAddress)) {
                cachedAddress = ip;
            }

            //TODO how can we know if a channel is kicked down?
            if (channel == null) {
                channel = bootstrap.connect(ip.getInetHost(), ip.getInetPort()).sync().channel();
            }    
        }
    }
    
    public void sendMessage(Message message) throws Exception {
        if (channel == null) {
            if (cachedAddress == null) {
                throw new Exception("client has no target");
            }
            channel = bootstrap.connect(cachedAddress.getInetHost(), cachedAddress.getInetPort()).sync().channel();
            channel.writeAndFlush(message);
        }
    }
    
    public EventExecutor getWorker() {
        return channel.eventLoop();
    }
}

package com.catizard.glass.network.client.abstractClient;

import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class RunnableNettyClient extends NettyClient implements Runnable {
    public Channel channel;
    public abstract void configure(Bootstrap bootstrap);
        
    //hooks
    public abstract void beforeRun();
    public abstract void afterRun();
    
    @Override
    public void run() {
        beforeRun();
        try {
            NioEventLoopGroup group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            configure(bootstrap);    
        } catch (Exception e) {
            e.printStackTrace();
        } 
        afterRun();
    }
}

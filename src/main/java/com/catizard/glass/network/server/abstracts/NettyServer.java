package com.catizard.glass.network.server.abstracts;

import com.catizard.glass.network.server.interfaces.Server;
import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class NettyServer implements Server {
    private ServerBootstrap serverBootstrap;
        
    public void configureChannelType(Class<? extends ServerChannel> clz) {
        serverBootstrap.channel(clz);
    }
    
    public void configureChannelHandler(ChannelInitializer<? extends Channel> initializer) {
        serverBootstrap.handler(initializer);
    }
    
    public void setupServer() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap.group(boss, worker);
    }

    @Override
    public void listenTo(InetAddress ip) {
        serverBootstrap.bind(ip.getInetHost(), ip.getInetPort());
    }
    
    public void listenTo(int port) {
        serverBootstrap.bind(port);
    }
}

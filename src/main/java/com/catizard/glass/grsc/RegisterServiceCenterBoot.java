package com.catizard.glass.grsc;

import com.catizard.glass.grsc.collection.MappedServices;
import com.catizard.glass.network.server.DefaultRunnableNettyServer;
import com.catizard.glass.network.server.abstracts.NettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;

public class RegisterServiceCenterBoot {
    public static void main(String[] args) {
        DefaultSingleNettyRegisterServiceCenter center = new DefaultSingleNettyRegisterServiceCenter(8080);
    }
}

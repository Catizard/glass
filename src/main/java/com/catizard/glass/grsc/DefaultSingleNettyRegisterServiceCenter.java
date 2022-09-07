package com.catizard.glass.grsc;

import com.catizard.glass.grsc.abstracts.AbstractSingleRegisterCenter;
import com.catizard.glass.grsc.collection.MappedServices;
import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.network.server.DefaultRunnableNettyServer;
import com.catizard.glass.network.server.abstracts.NettyServer;
import com.catizard.glass.network.server.interfaces.Server;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;

public class DefaultSingleNettyRegisterServiceCenter extends AbstractSingleRegisterCenter {
    public DefaultSingleNettyRegisterServiceCenter(int port) {
        NettyServer server = new DefaultRunnableNettyServer();
        ServicesCollection servicesCollection = MappedServices.initMappedServices(new HashMap<>());
        server.configureChannelType(NioServerSocketChannel.class);
        server.configureChannelHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new FetchServiceRequestMessageHandler());
                ch.pipeline().addLast(new RegisterServiceRequestMessageHandler());
            }
        });
        server.listenTo(port);
        super.setServer(server);
        super.setServices(servicesCollection);
    }
}

package com.catizard.glass.center;

import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterServiceCenter {
    public static Map<String, List<InetAddress>> registeredServices = new HashMap<>();
    private static class RegisterServer extends Server {
        public RegisterServer(InetAddress address, String name) {
            super(address, name);
        }
        
        @Override
        public void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new RegisterServiceRequestMessageHandler());
            ch.pipeline().addLast(new FetchServiceRequestMessageHandler());
        }
    }
    public static void main(String[] args) {
        new Thread(new RegisterServer(new InetAddress("localhost", 8080), "register server")).start();
    }
}

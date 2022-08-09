package com.catizard.glass.utils;

import com.catizard.glass.message.Message;
import com.catizard.glass.message.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public abstract class Client {
    private Channel channel = null;
    private final Object LOCK = new Object();
    
    public Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            
            initChannel();
            return channel;
        }
    }
    
    private void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    Client.this.initChannel(ch);
                }
            });
            InetAddress ip = getInetAddress();
            System.out.println("[Client] connect to ip {" + ip.getInetHost() + ":" + ip.getInetPort() + "}");
            channel = bootstrap.connect(ip.getInetHost(), ip.getInetPort()).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("client error");
        }
    }
    
    public void sendMessage(Message message) {
        getChannel().writeAndFlush(message);
        System.out.println("[Client] send message");
    }
    
    public abstract InetAddress getInetAddress();

    public abstract void initChannel(SocketChannel ch);
}

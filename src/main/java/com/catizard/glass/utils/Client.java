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
    private NioEventLoopGroup group = null;
    private Bootstrap bootstrap;
    private Channel channel;
    private int clientId;
    private int requestId = 0;

    public int getClientId() {
        return clientId;
    }

    public int getRequestId() {
        int result = requestId;
        requestId++;
        return result;
    }
    

    private void setup() {
        //set up client
        try {
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    Client.this.initChannel(ch);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set up some variables
        clientId = RandomGenerator.random.nextInt(Integer.MAX_VALUE);
        requestId = 0;
        System.out.println("a new Client set up, client id is " + clientId + " request id is " + requestId);
    }
    public Client() {
        setup();
    }
    
    public Client(InetAddress address) {
        setup();
        try {
            channel = bootstrap.connect(address.getInetHost(), address.getInetPort()).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Channel getChannel() {
        if (channel == null) {
            throw new RuntimeException("Client didn't connect to any Server!");
        }
        return channel;
    }

    public void shutdownGracefully() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
    
    public void connect(InetAddress address) {
        if (bootstrap == null) {
            throw new RuntimeException("called connect before init client");
        }
        try {
            channel = bootstrap.connect(address.getInetHost(), address.getInetPort()).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void initChannel(SocketChannel ch);
}

package com.catizard.glass.utils;

import com.catizard.glass.message.MessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public abstract class Server implements Runnable {
    private InetAddress address = new InetAddress("NOSET", -1);
    private String name;
    
    public Server(InetAddress address, String name) {
        this.address = address;
        this.name = name;
    }
    
    public InetAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public abstract void initChannel(NioSocketChannel ch) throws Exception;
    
    @Override
    public void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
    
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    Server.this.initChannel(ch);
                }
            });
            
            if (address.getInetPort() == -1) {
                throw new RuntimeException("server address didn't set");
            }

            Channel channel = serverBootstrap.bind(address.getInetHost(), address.getInetPort()).sync().channel();
            channel.closeFuture().sync();
            System.out.println("server " + name + " closed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public String toString() {
        return "Server{" +
                "address=" + address +
                ", name='" + name + '\'' +
                '}';
    }
}


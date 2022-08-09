package com.catizard.ptp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.DefaultPromise;
import com.catizard.ptp.Message.RPCRequestMessage;
import com.catizard.ptp.services.HelloService;

import java.lang.reflect.Proxy;
import java.util.Random;

public class Client {
    private Channel channel = null;
    private final Object LOCK = new Object();
    private int ClientID = 0;
    private int RequestID;

    public Client() {
        Random random = new Random();
        ClientID = random.nextInt(Integer.MAX_VALUE);
        RequestID = 0;
    }

    public <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            //TODO make it concurrent safe
            ++RequestID;
            RPCRequestMessage message = new RPCRequestMessage(
                    ClientID,
                    RequestID,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            getChannel().writeAndFlush(message);

            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RPCResponseMessageHandler.waitChan.put(new RequestIdentify(ClientID, RequestID), promise);
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        });
        return (T)o;
    }
    
    private Channel getChannel() {
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
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
                    ch.pipeline().addLast(new MessageCodec());
                    ch.pipeline().addLast(new RPCResponseMessageHandler());
                }
            });
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("client error");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
//        System.out.println(client.ClientID);
        HelloService helloService = (HelloService) client.getProxyService(HelloService.class);
        String hello = helloService.hello("World!");
        System.out.println(hello);
    }
}

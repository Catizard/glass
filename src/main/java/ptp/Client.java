package ptp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import ptp.Message.RPCRequestMessage;
import ptp.services.HelloService;

import javax.print.attribute.standard.RequestingUserName;
import java.lang.reflect.Proxy;
import java.util.Random;

public class Client {
    private static Channel channel = null;
    private static final Object LOCK = new Object();
    private static int ClientID = 0;
    private static int RequestID;

    public Client() {
        Random random = new Random();
        ClientID = random.nextInt();
        RequestID = 0;
    }

    public static <T> T getProxyService(Class<T> serviceClass) {
        //TODO gen ClientID;
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
    
    private static Channel getChannel() {
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
    
    private static void initChannel() {
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
        HelloService helloService = (HelloService) getProxyService(HelloService.class);
        String hello = helloService.hello("IT'S A MESSAGE");
        System.out.println(hello);
    }
}

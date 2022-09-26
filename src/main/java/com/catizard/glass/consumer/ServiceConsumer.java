package com.catizard.glass.consumer;


import com.catizard.glass.center.RegisterCenterClient;
import com.catizard.glass.center.balance.Balance;
import com.catizard.glass.center.balance.CircleBalance;
import com.catizard.glass.center.utils.wrappers.DefaultServiceNameFactory;
import com.catizard.glass.center.utils.wrappers.DefaultServicePathFactory;
import com.catizard.glass.center.utils.wrappers.ServiceNameFactory;
import com.catizard.glass.center.utils.wrappers.ServicePathFactory;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.RPCRequestMessage;
import com.catizard.glass.service.HelloService;
import com.catizard.glass.service.RPCService;
import com.catizard.glass.utils.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;

public class ServiceConsumer {
    static class RPCClient {
        private InetAddress address;
        private Bootstrap bootstrap = new Bootstrap();
        private NioEventLoopGroup group = new NioEventLoopGroup();
        private int clientId = RandomGenerator.random.nextInt(Integer.MAX_VALUE);
        private int requestId = 0;
        
        public RPCClient(InetAddress ip) {
            address = ip;
            try {
                bootstrap.group(group);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
                        ch.pipeline().addLast(new MessageCodec());
                        ch.pipeline().addLast(new RPCResponseMessageHandler());
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public <T> T getProxyService(InetAddress remoteAddress, Class<T> serviceClass) {
            //send this to the assigned address
            ClassLoader classLoader = serviceClass.getClassLoader();
            Class<?>[] interfaces = new Class[]{serviceClass};
            Object o = Proxy.newProxyInstance(classLoader, interfaces, ((proxy, method, args) -> {
                requestId++;
                RequestIdentify id = new RequestIdentify(clientId, requestId);
                //TODO make it configurable
                String serviceName = serviceClass.getAnnotation(RPCService.class).value();
                if (serviceName == null || serviceName.equals("")) {
                    serviceName = serviceClass.getName();
                    String[] split = serviceName.split("\\.");
                    serviceName = split[split.length - 1];
                }
                RPCRequestMessage message = new RPCRequestMessage(
                        new RequestIdentify(clientId, requestId), 
                        serviceName,
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        args
                );
                System.out.println("[Consumer] send RPC message [" + message + "]");
                /*TODO actually I think send request message is a async op
                    so should we use UDP instead of TCP? because there needs to wait tcp sync*/
                Channel channel = bootstrap.connect(remoteAddress.getInetHost(), remoteAddress.getInetPort()).sync().channel();
                channel.writeAndFlush(message);
                DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
                RequestPromiseChannel.waitCh.put(id, promise);
                promise.await();
                if (promise.isSuccess()) {
                    return promise.getNow();
                } else {
                    throw new RuntimeException(promise.cause());
                }
            }));
            return (T)o;
        }
    }
    private InetAddress centerAddress;
    private final RPCClient rpcClient;
    private RegisterCenterClient rcc;
    private ServiceNameFactory nameFactory;

    public ServiceConsumer(InetAddress centerAddress, Balance balance, ServiceNameFactory nameFactory, ServicePathFactory pathFactory) {
        this.centerAddress = centerAddress;
        this.nameFactory = nameFactory;
        this.rpcClient = new RPCClient(centerAddress);
        this.rcc = new RegisterCenterClient(balance, pathFactory);
    }

    public <T> T getProxyService(Class<T> clz) throws InterruptedException {
        String serviceName = clz.getAnnotation(RPCService.class).value();
        if (serviceName == null || serviceName.equals("")) {
            serviceName = nameFactory.InterfaceNameToServiceName(serviceName);
        }

        //fetch address
        try {
            String result = rcc.fetchService(serviceName);
            //TODO make a function cast String to InetAddress
            InetAddress target = new InetAddress(result.split(":")[0], Integer.parseInt(result.split(":")[1]));
            return rpcClient.getProxyService(target, clz);    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        ServiceConsumer serviceConsumer = new ServiceConsumer(
                new InetAddress("localhost", 8080), 
                new CircleBalance(),
                new DefaultServiceNameFactory(),
                new DefaultServicePathFactory());
        HelloService helloservice = serviceConsumer.getProxyService(HelloService.class);
        if (helloservice == null) {
            throw new Exception("uh?");
        }
        String result = helloservice.sayHello("world!");
        System.out.println(result);
    }
}

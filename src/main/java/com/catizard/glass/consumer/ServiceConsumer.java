package com.catizard.glass.consumer;

import com.catizard.glass.message.FetchServiceRequestMessage;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.RPCRequestMessage;
import com.catizard.glass.service.HelloService;
import com.catizard.glass.service.RPCService;
import com.catizard.glass.utils.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;

public class ServiceConsumer {
    static class FetchClient extends Client {
        private InetAddress ip;
        private final int clientId = (RandomGenerator.random.nextInt(Integer.MAX_VALUE));
        private int requestId = 0;
        public FetchClient(InetAddress ip) {
            this.ip = ip;
        }

        @Override
        public InetAddress getInetAddress() {
            return ip;
        }

        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new FetchServiceResponseMessageHandler());
        }
        
        public InetAddress sendFetchRequestMessage(String serviceName) throws InterruptedException {
            requestId++;
            RequestIdentify id = new RequestIdentify(clientId, requestId);
            FetchServiceRequestMessage message = new FetchServiceRequestMessage(serviceName, id);
            System.out.println("[Consumer] send fetch message [" + message + "]");
            super.sendMessage(message);
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RequestPromiseChannel.waitCh.put(id, promise);
            promise.await();
            if (promise.isSuccess()) {
                return (InetAddress) promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        }
    }
    static class RPCClient extends Client {
        private InetAddress ip;
        private final int clientId = RandomGenerator.random.nextInt(Integer.MAX_VALUE);
        private int requestId = 0;

        public RPCClient(InetAddress ip) {
            this.ip = ip;
        }
        
        public <T> T getProxyService(InetAddress remoteAddress, Class<T> serviceClass) {
            //send this to the assigned address
            ip = remoteAddress;
            ClassLoader classLoader = serviceClass.getClassLoader();
            Class<?>[] interfaces = new Class[]{serviceClass};
            Object o = Proxy.newProxyInstance(classLoader, interfaces, ((proxy, method, args) -> {
                //TODO send a message and fetch a result processes are the same, rewrite in base Client
                ++requestId;
                RequestIdentify id = new RequestIdentify(clientId, requestId);
                RPCRequestMessage message = new RPCRequestMessage(
                        new RequestIdentify(clientId, requestId), 
                        serviceClass.getAnnotation(RPCService.class).value(),
                        method.getName(),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        args
                );
                System.out.println("[Consumer] send RPC message [" + message + "]");
                super.sendMessage(message);
                DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
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
        
        @Override
        public InetAddress getInetAddress() {
            return ip;
        }
        
        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new RPCResponseMessageHandler());
        }
    }
    private InetAddress centerAddress;
    private final FetchClient fetchClient;
    private final RPCClient rpcClient;
    
    public ServiceConsumer(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
        this.fetchClient = new FetchClient(centerAddress);
        this.rpcClient = new RPCClient(centerAddress);
    }
    
    public InetAddress fetchService(String serviceName) throws InterruptedException {
        return fetchClient.sendFetchRequestMessage(serviceName);
    }

    public static void main(String[] args) throws InterruptedException {
        ServiceConsumer serviceConsumer = new ServiceConsumer(new InetAddress("localhost", 8080));
        InetAddress helloServiceAddress = serviceConsumer.fetchService("HelloService");
        HelloService helloService = serviceConsumer.rpcClient.getProxyService(helloServiceAddress, HelloService.class);
        System.out.println(helloService.sayHello("world!"));
    }
}

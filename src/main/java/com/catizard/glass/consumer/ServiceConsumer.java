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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServiceConsumer {
    static class FetchClient extends Client {
        public FetchClient(InetAddress ip) {
            super(ip);
        }

        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new FetchServiceResponseMessageHandler());
        }
        
        public List<InetAddress> sendFetchRequestMessage(String serviceName) throws InterruptedException {
            int clientId = getClientId(), requestId = getRequestId();
            RequestIdentify id = new RequestIdentify(clientId, requestId);
            FetchServiceRequestMessage message = new FetchServiceRequestMessage(serviceName, id);
            System.out.println("[Consumer] send fetch message [" + message + "]");
            getChannel().writeAndFlush(message);
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RequestPromiseChannel.waitCh.put(id, promise);
            promise.await(TimeUnit.SECONDS.toMillis(2));
            if (promise.isSuccess()) {
                return (List<InetAddress>) promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        }
    }
    static class RPCClient extends Client {
        private InetAddress address;
        public RPCClient(InetAddress ip) {
            super(ip);
            address = ip;
        }
        
        public <T> T getProxyService(InetAddress remoteAddress, Class<T> serviceClass) {
            if (!remoteAddress.equals(address)) {
                //reconnect to remote address
                try {
                    connect(remoteAddress);
                } catch (InterruptedException e) {
                    //TODO should send a message to center
                    System.out.println("cannot connect to remote address");
                    throw new RuntimeException(e);
                }
            }
            //send this to the assigned address
            ClassLoader classLoader = serviceClass.getClassLoader();
            Class<?>[] interfaces = new Class[]{serviceClass};
            Object o = Proxy.newProxyInstance(classLoader, interfaces, ((proxy, method, args) -> {
                int clientId = getClientId(), requestId = getRequestId();
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
                getChannel().writeAndFlush(message);
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
    
    public List<InetAddress> fetchService(String serviceName) throws InterruptedException {
        return fetchClient.sendFetchRequestMessage(serviceName);
    }

    public <T> T getProxyService(Class<T> clz) throws InterruptedException {
        String serviceName = clz.getAnnotation(RPCService.class).value();
        List<InetAddress> serviceAddressList = fetchService(serviceName);
        if (serviceAddressList == null || serviceAddressList.isEmpty()) {
            return null;
        }
        //TODO need a balance rule to choose which remote should be sent
        return rpcClient.getProxyService(serviceAddressList.get(0), clz);
    }

    public static void main(String[] args) throws InterruptedException {
        ServiceConsumer serviceConsumer = new ServiceConsumer(new InetAddress("localhost", 8080));
        HelloService helloservice = serviceConsumer.getProxyService(HelloService.class);
        String result = helloservice.sayHello("world!");
        System.out.println(result);
    }
}

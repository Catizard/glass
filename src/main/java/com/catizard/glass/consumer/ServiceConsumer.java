package com.catizard.glass.consumer;

import com.catizard.glass.message.FetchServiceRequestMessage;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.utils.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.ArrayList;

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
    
    private InetAddress centerAddress;
    private final FetchClient fetcher;
    
    public ServiceConsumer(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
        this.fetcher = new FetchClient(centerAddress);
    }
    
    public InetAddress fetchService(String serviceName) throws InterruptedException {
        return fetcher.sendFetchRequestMessage(serviceName);
    }

    public static void main(String[] args) throws InterruptedException {
        ServiceConsumer serviceConsumer = new ServiceConsumer(new InetAddress("localhost", 8080));
        InetAddress address = serviceConsumer.fetchService("HelloService");
        System.out.println(address);
    }
}

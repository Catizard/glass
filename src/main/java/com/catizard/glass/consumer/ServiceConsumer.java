package com.catizard.glass.consumer;

import com.catizard.glass.message.FetchServiceRequestMessage;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.ArrayList;

public class ServiceConsumer {
    static class FetchClient extends Client {
        private InetAddress ip;
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
            //ch.pipeline().addLast(new FetchServicesResponseMessageHandler());
        }
        
        public ArrayList<InetAddress> sendFetchRequestMessage(String serviceName) {
            FetchServiceRequestMessage message = new FetchServiceRequestMessage(serviceName);
            super.sendMessage(message);
            //TODO handle response message
            return new ArrayList<>();
        }
    }
    
    private InetAddress centerAddress;
    private final FetchClient fetcher;
    
    public ServiceConsumer(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
        this.fetcher = new FetchClient(centerAddress);
    }
    
    public void fetchService(String serviceName) {
        fetcher.sendFetchRequestMessage(serviceName);
    }

    public static void main(String[] args) {
        ServiceConsumer serviceConsumer = new ServiceConsumer(new InetAddress("localhost", 8080));
        serviceConsumer.fetchService("HelloService");
    }
}

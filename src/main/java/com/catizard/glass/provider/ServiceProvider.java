package com.catizard.glass.provider;

import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.RegisterServiceRequestMessage;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ServiceProvider {
    static class RegisterClient extends Client {
        private InetAddress ip;

        public RegisterClient(InetAddress ip) {
            this.ip = ip;
        }

        @Override
        public InetAddress getInetAddress() {
            return ip;
        }

        @Override
        public void initChannel(SocketChannel ch) {
            System.out.println("[ServiceRegister] called child's initChannel()");
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
        }

        public void sendRegisterMessage(String serviceName, InetAddress serviceAddress) {
            RegisterServiceRequestMessage message = new RegisterServiceRequestMessage(serviceName, serviceAddress.getInetHost(), serviceAddress.getInetPort());
            super.sendMessage(message);
        }
    }
    //TODO ServiceProvider should automatically scan all the services and register them
    private InetAddress centerAddress;
    private final RegisterClient register;
    
    public ServiceProvider(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
        this.register = new RegisterClient(centerAddress);
    }
    
    public void registerService(String serviceName, InetAddress serviceAddress) {
        register.sendRegisterMessage(serviceName, serviceAddress);
    }

    public static void main(String[] args) {
        ServiceProvider serviceProvider = new ServiceProvider(new InetAddress("localhost", 8080));
        serviceProvider.registerService("HelloService", new InetAddress("localhost", 9090));
    }
}

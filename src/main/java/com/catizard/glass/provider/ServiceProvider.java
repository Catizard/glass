package com.catizard.glass.provider;

import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.RegisterServiceRequestMessage;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.Server;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private static class ProviderServer extends Server {
        public ProviderServer(InetAddress address, String name) {
            super(address, name);
        }

        @Override
        public void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new RPCRequestMessageHandler());
        }
    }
    
    public static void main(String[] args) {
        ProviderServer server = new ProviderServer(new InetAddress("localhost", 9090), "provider server");
        new Thread(server).start();
        ServiceProvider serviceProvider = new ServiceProvider(new InetAddress("localhost", 8080));
        serviceProvider.registerService("HelloService", new InetAddress("localhost", 9090));
    }
}

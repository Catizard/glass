package com.catizard.glass.provider;

import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.RegisterServiceRequestMessage;
import com.catizard.glass.service.RPCService;
import com.catizard.glass.utils.AnnotationScanner;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.Server;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

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
    private InetAddress centerAddress;
    private InetAddress listenAddress;
    private final RegisterClient register;
    private Thread server;
    
    public ServiceProvider(InetAddress listenAddress, InetAddress centerAddress, String... packNames) {
        this.listenAddress = listenAddress;
        this.centerAddress = centerAddress;
        this.register = new RegisterClient(centerAddress);
        for (String packName : packNames) {
            scanAndRegister(listenAddress, packName);
        }
        this.server = new Thread(new ProviderServer(listenAddress, "provider server"));
        this.server.start();
    }

    public InetAddress getCenterAddress() {
        return centerAddress;
    }

    public void setCenterAddress(InetAddress centerAddress) {
        this.centerAddress = centerAddress;
    }

    public InetAddress getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(InetAddress listenAddress) {
        this.listenAddress = listenAddress;
    }

    public void scanAndRegister(InetAddress listenAddress, String packName) {
        List<Class<?>> classList = AnnotationScanner.scan(packName, RPCService.class);
        for (Class<?> clz : classList) {
            if (!clz.isInterface()) {
                String serviceName = clz.getAnnotation(RPCService.class).value();
                registerService(serviceName, listenAddress);
                try {
                    ServicesFactory.setService(serviceName, clz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public void close() {
        this.server.interrupt();
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
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        InetAddress selfAddress = new InetAddress("localhost", 9090);
        InetAddress centerAddress = new InetAddress("localhost", 8080);
        //run server listen to port 9090
        
        ServiceProvider serviceProvider = new ServiceProvider(selfAddress, centerAddress, "com.catizard.glass.service");
//        serviceProvider.close();
    }
}

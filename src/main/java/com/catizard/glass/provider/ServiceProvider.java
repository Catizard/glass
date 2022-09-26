package com.catizard.glass.provider;
import com.catizard.glass.center.RegisterCenterClient;
import com.catizard.glass.center.balance.Balance;
import com.catizard.glass.center.balance.CircleBalance;
import com.catizard.glass.center.utils.wrappers.DefaultServiceNameFactory;
import com.catizard.glass.center.utils.wrappers.DefaultServicePathFactory;
import com.catizard.glass.center.utils.wrappers.ServiceNameFactory;
import com.catizard.glass.center.utils.wrappers.ServicePathFactory;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.service.RPCService;
import com.catizard.glass.utils.AnnotationScanner;
import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServiceProvider {
    private InetAddress centerAddress;
    private InetAddress listenAddress;
    private Thread threadServer;
    private RegisterCenterClient rcc;
    private ServiceNameFactory nameFactory;
    public ServiceProvider(
            InetAddress listenAddress, 
            InetAddress centerAddress, 
            Balance balance,
            ServiceNameFactory nameFactory,
            ServicePathFactory pathFactory,
            String... packNames
            ) {
        this.listenAddress = listenAddress;
        this.centerAddress = centerAddress;
        this.rcc = new RegisterCenterClient(balance, pathFactory);
        this.nameFactory = nameFactory;
        for (String packName : packNames) {
            scanAndRegister(listenAddress, packName);
        }
        this.threadServer = new Thread(new ProviderServer(listenAddress));
        this.threadServer.start();
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
                String serviceName = constructServiceName(clz);
    
                //TODO what if rcc failed?
                //send it to zk server
                rcc.provideService(serviceName, listenAddress.toString());
                
                //register in ServicesFactory
                try {
                    ServicesFactory.setService(serviceName, clz.newInstance());
                    System.out.println("set " + serviceName);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String constructServiceName(Class<?> clz) {
        String serviceName = clz.getAnnotation(RPCService.class).value();
        if ("".equals(serviceName)) {
            serviceName = clz.getName();
            //TODO make it configurable
            if(serviceName.endsWith("Impl")) {
                serviceName = nameFactory.ImplementationNameToServiceName(serviceName);
            }
        }
        return serviceName;
    }

    public void close() {
        this.threadServer.interrupt();
    }
    
    private static class ProviderServer implements Runnable {
        private InetAddress listenAddress;
        private ServerBootstrap serverBootstrap = new ServerBootstrap();
        private NioEventLoopGroup boss = new NioEventLoopGroup();
        private NioEventLoopGroup worker = new NioEventLoopGroup();
        public ProviderServer(InetAddress listenAddress) {
            this.listenAddress = listenAddress;
            serverBootstrap.group(boss, worker);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
                    ch.pipeline().addLast(new MessageCodec());
                    ch.pipeline().addLast(new RPCRequestMessageHandler());
                    ch.pipeline().addLast(new HeartbeatRequestMessageHandler());
                }
            });
        }

        public void close() {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

        @Override
        public void run() {
            try {
                Channel channel = serverBootstrap.bind(listenAddress.getInetHost(), listenAddress.getInetPort()).sync().channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        InetAddress selfAddress = new InetAddress("localhost", 9090);
        InetAddress centerAddress = new InetAddress("localhost", 2181);
        //run server listen to port 9090
        
        ServiceProvider serviceProvider = new ServiceProvider(
                selfAddress, 
                centerAddress,
                new CircleBalance(),
                new DefaultServiceNameFactory(),
                new DefaultServicePathFactory(),
                "com.catizard.glass.service"
                );
//        serviceProvider.close();
    }
}

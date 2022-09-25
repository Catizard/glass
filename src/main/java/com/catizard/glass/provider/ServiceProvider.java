package com.catizard.glass.provider;
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
    private CuratorFramework zkCli;
    private InetAddress centerAddress;
    private InetAddress listenAddress;
    private Thread threadServer;
    
    public ServiceProvider(InetAddress listenAddress, InetAddress centerAddress, String... packNames) {
        this.listenAddress = listenAddress;
        this.centerAddress = centerAddress;
        this.zkCli = CuratorFrameworkFactory.newClient(centerAddress.toString(), 
                new ExponentialBackoffRetry(1000, 3));
        this.zkCli.start();
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
                String serviceName = clz.getAnnotation(RPCService.class).value();
                if ("".equals(serviceName)) {
                    serviceName = clz.getName();
                    //TODO make it configurable
                    if(serviceName.endsWith("Impl")) {
                        serviceName = serviceName.substring(0, serviceName.indexOf("Impl"));
                    }
                }
                System.out.println(serviceName);

                String servicePath = serviceName.replace('.', '/');
                servicePath = "/" + servicePath + "/server";
                System.out.println(servicePath);

                try {
                    System.out.println(listenAddress.toString());
                    zkCli.create().creatingParentsIfNeeded().
                            withMode(CreateMode.EPHEMERAL_SEQUENTIAL).
                            forPath(servicePath, listenAddress.toString().getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] split = serviceName.split("\\.");
                serviceName = split[split.length - 1];
                try {
                    ServicesFactory.setService(serviceName, clz.newInstance());
                    System.out.println("set " + serviceName);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
        
        ServiceProvider serviceProvider = new ServiceProvider(selfAddress, centerAddress, "com.catizard.glass.service");
//        serviceProvider.close();
    }
}

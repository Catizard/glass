package com.catizard.glass.grsc;

import com.catizard.glass.message.HeartbeatRequestMessage;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.RequestIdentify;
import com.catizard.glass.utils.Server;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RegisterServiceCenter {
    private static Map<String, List<InetAddress>> registeredServices = new ConcurrentHashMap<>();
    private static Map<InetAddress, Boolean> registeredAddresses = new ConcurrentHashMap<>();
    
    public static void registerService(String serviceName, InetAddress remoteAddress) {
        registeredServices.computeIfAbsent(serviceName, k -> new ArrayList<>());
        List<InetAddress> list = registeredServices.get(serviceName);
        
        boolean exists = false;
        for (InetAddress address : list) {
            if (address.equals(remoteAddress)) {
                exists = true;
            }
        }
        
        if (!exists) {
            registeredServices.get(serviceName).add(remoteAddress);
            registeredAddresses.put(remoteAddress, true);    
        }
    }
    public static List<InetAddress> fetchServiceAddresses(String serviceName) {
        return registeredServices.get(serviceName);
    }
    private static class RegisterServer extends Server {
        public RegisterServer(InetAddress address, String name) {
            super(address, name);
        }
        
        @Override
        public void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            ch.pipeline().addLast(new RegisterServiceRequestMessageHandler());
            ch.pipeline().addLast(new FetchServiceRequestMessageHandler());
        }
    }
    private static class HeartbeatClient extends Client {
        private InetAddress address;
        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
            ch.pipeline().addLast(new MessageCodec());
            //ch.pipeline().addLast(new HeartbeatResponseMessageHandler());
        }
    }
    private static class HeartBeatHelper implements Runnable {
        @Override
        public void run() {
            HeartbeatClient heartbeatClient = new HeartbeatClient();
            while (true) {
                try {
                    for (Map.Entry<InetAddress, Boolean> entry : registeredAddresses.entrySet()) {
                        InetAddress address = entry.getKey();
                        try {
                            if (!address.equals(heartbeatClient.address)) {
                                heartbeatClient.connect(address);
                            }
                            HeartbeatRequestMessage heartbeatRequestMessage = new HeartbeatRequestMessage(new RequestIdentify(heartbeatClient.getClientId(), heartbeatClient.getRequestId()));
                            heartbeatClient.getChannel().writeAndFlush(heartbeatRequestMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //TODO if a remote address couldn't receive heartbeat too many times, remove it
                            System.out.println("heartclient connected to " + address + " failed");
                        }
                    }
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        new Thread(new RegisterServer(new InetAddress("localhost", 8080), "register server"), "register server thread").start();
        new Thread(new HeartBeatHelper(), "center heartbeat thread").start();
    }
}

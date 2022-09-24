package com.catizard.glass.grsc;

import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.message.RegisterServiceRequestMessage;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RegisterServiceRequestMessageHandler extends SimpleChannelInboundHandler<RegisterServiceRequestMessage> {
    private final ServicesCollection collection;

    public RegisterServiceRequestMessageHandler(ServicesCollection collection) {
        this.collection = collection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterServiceRequestMessage msg) throws Exception {
        System.out.println("[Center] received register request message [" + msg + "]");
        try {
            String serviceName = msg.getServiceName();
            InetAddress serviceAddress = msg.getServiceAddress();
            collection.addService(serviceName, serviceAddress);
            System.out.println("[Center] registered a new Service: name " + serviceName + " address " + serviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

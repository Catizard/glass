package com.catizard.glass.center;

import com.catizard.glass.message.ServiceRegisterRequestMessage;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServiceRegisterRequestHandler extends SimpleChannelInboundHandler<ServiceRegisterRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServiceRegisterRequestMessage msg) throws Exception {
        System.out.println("[Center] received register request message [" + msg + "]");
        try {
            String serviceName = msg.getServiceName();
            InetAddress serviceAddress = msg.getServiceAddress();
            RegisterServiceCenter.registeredServices.put(serviceName, serviceAddress);
            System.out.println("[Center] registered a new Service: name " + serviceName + " address " + serviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

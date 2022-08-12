package com.catizard.glass.center;

import com.catizard.glass.message.RegisterServiceRequestMessage;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class RegisterServiceRequestMessageHandler extends SimpleChannelInboundHandler<RegisterServiceRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterServiceRequestMessage msg) throws Exception {
        System.out.println("[Center] received register request message [" + msg + "]");
        try {
            String serviceName = msg.getServiceName();
            InetAddress serviceAddress = msg.getServiceAddress();
            List<InetAddress> list = RegisterServiceCenter.registeredServices.get(serviceName);
            if (list == null) {
                list = new ArrayList<>();
                list.add(serviceAddress);
                RegisterServiceCenter.registeredServices.put(serviceName, list);
            } else {
                list.add(serviceAddress);
            }
            
            System.out.println("[Center] registered a new Service: name " + serviceName + " address " + serviceAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

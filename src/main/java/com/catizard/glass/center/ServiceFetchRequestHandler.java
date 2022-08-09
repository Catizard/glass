package com.catizard.glass.center;

import com.catizard.glass.message.FetchServiceRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServiceFetchRequestHandler extends SimpleChannelInboundHandler<FetchServiceRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchServiceRequestMessage msg) throws Exception {
        System.out.println("[Center] received register request message [" + msg + "]");
        try {
            String serviceName = msg.getServiceName();
            System.out.println("[Center] fetched service name is " + serviceName + " and the result is " + RegisterServiceCenter.registeredServices.get(serviceName));
            //TODO need response
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

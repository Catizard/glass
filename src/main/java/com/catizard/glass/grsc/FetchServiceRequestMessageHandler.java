package com.catizard.glass.grsc;

import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.message.FetchServiceRequestMessage;
import com.catizard.glass.message.FetchServiceResponseMessage;
import com.catizard.glass.utils.RequestIdentify;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FetchServiceRequestMessageHandler extends SimpleChannelInboundHandler<FetchServiceRequestMessage> {
    private ServicesCollection collection;
    public FetchServiceRequestMessageHandler(ServicesCollection collection) {
        this.collection = collection;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchServiceRequestMessage msg) throws Exception {
        System.out.println("[Center] received fetch request message [" + msg + "]");
        FetchServiceResponseMessage responseMessage = new FetchServiceResponseMessage();
        try {
            String serviceName = msg.getServiceName();
            System.out.println("[Center] fetched service name is " + serviceName + " and the result is " + collection.getServices(serviceName));
            RequestIdentify id = msg.getId();
            responseMessage.setAddressList(collection.getServices(serviceName));
            responseMessage.setId(id);
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getCause().getMessage();
            responseMessage.setExceptionValue(new Exception("remote error: " + err));
        }
        
        ctx.writeAndFlush(responseMessage);
    }
}

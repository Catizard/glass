package com.catizard.glass.provider;

import com.catizard.glass.message.RPCRequestMessage;
import com.catizard.glass.message.RPCResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class RPCRequestMessageHandler extends SimpleChannelInboundHandler<RPCRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequestMessage msg) throws Exception {
        System.out.println("[Server] received RPCRequestMessage: " + msg);
        RPCResponseMessage response = new RPCResponseMessage();
        response.setId(msg.getId());
        try {
            Object service = ServicesFactory.getServices(msg.getServiceName());
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object result = method.invoke(service, msg.getParameterValues());
            System.out.println("[Server] received RPCRequestMessage, result is " + result);
            response.setReturnValue(result);
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getCause().getMessage();
            response.setExceptionValue(new Exception("remote error: " + err));
        }
        ctx.writeAndFlush(response);
    }
}

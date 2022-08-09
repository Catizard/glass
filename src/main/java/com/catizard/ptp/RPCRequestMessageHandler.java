package com.catizard.ptp;

import com.catizard.ptp.Message.RPCRequestMessage;
import com.catizard.ptp.Message.RPCResponseMessage;
import com.catizard.ptp.services.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class RPCRequestMessageHandler extends SimpleChannelInboundHandler<RPCRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequestMessage msg) throws Exception {
        RPCResponseMessage rpcResponseMessage = new RPCResponseMessage();
        rpcResponseMessage.setRequestIdentify(msg.getRequestIdentify());
        try {
            Object services = ServicesFactory.getServices(Class.forName(msg.getInterfaceName()));
            Method method = services.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(services, msg.getParameterValues());
//            System.out.println(invoke);
            rpcResponseMessage.setReturnValue(invoke);    
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getCause().getMessage();
            rpcResponseMessage.setExceptionValue(new Exception("remote error: " + err));
        }
        ctx.writeAndFlush(rpcResponseMessage);
    } 
}

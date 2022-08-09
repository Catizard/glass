package com.catizard.ptp;

import com.catizard.ptp.Message.RPCResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

public class RPCResponseMessageHandler extends SimpleChannelInboundHandler<RPCResponseMessage> {
    public static Map<RequestIdentify, Promise<Object>> waitChan = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponseMessage msg) throws Exception {
        Promise<Object> promise = waitChan.get(msg.getRequestIdentify());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}

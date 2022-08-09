package com.catizard.glass.consumer;

import com.catizard.glass.message.RPCResponseMessage;
import com.catizard.glass.utils.RequestIdentify;
import com.catizard.glass.utils.RequestPromiseChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public class RPCResponseMessageHandler extends SimpleChannelInboundHandler<RPCResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponseMessage msg) throws Exception {
        RequestIdentify id = msg.getId();
        Promise<Object> promise = RequestPromiseChannel.waitCh.get(id);
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

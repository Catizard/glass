package com.catizard.glass.consumer;

import com.catizard.glass.message.FetchServiceResponseMessage;
import com.catizard.glass.utils.InetAddress;
import com.catizard.glass.utils.RequestPromiseChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public class FetchServiceResponseMessageHandler extends SimpleChannelInboundHandler<FetchServiceResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchServiceResponseMessage msg) throws Exception {
        System.out.println("ResponseMessageHandler received response [" + msg + "]");
        Promise<Object> promise = RequestPromiseChannel.waitCh.get(msg.getId());
        if (promise != null) {
            InetAddress address = msg.getAddress();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(address);
            }
        }
    }
}

package com.catizard.glass.provider;

import com.catizard.glass.message.HeartbeatRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartbeatRequestMessageHandler extends SimpleChannelInboundHandler<HeartbeatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatRequestMessage msg) throws Exception {
        System.out.println(msg);
    }
}

package com.catizard.glass.network.client.handlers;

import com.catizard.glass.network.utils.RequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DefaultRequestEncoder extends MessageToByteEncoder<RequestMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestMessage msg, ByteBuf out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}

package com.catizard.glass.network.client.handlers;

import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.network.utils.message.AbstractMessage;
import com.catizard.glass.network.utils.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class DefaultMessageCodec extends MessageToMessageCodec<ByteBuf, AbstractMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractMessage msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeInt(msg.getMessageType());
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> outList) throws Exception {
        int messageType = msg.readInt();
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(bytes, AbstractMessage.messageClasses.get(messageType));
        outList.add(message);
    }
}

package com.catizard.glass.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeInt(msg.getMessageType());
        //TODO provide more serialize chooses
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("[Client] encoded message " + msg);
        byte[] bytes = objectMapper.writeValueAsBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int messageType = msg.readInt();
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);
        
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(bytes, Message.getMessageClass(messageType));
        out.add(message);
    }
}

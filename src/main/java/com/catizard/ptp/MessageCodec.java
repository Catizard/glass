package com.catizard.ptp;

import com.catizard.ptp.Message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
//        System.out.println("now encoding " + msg);
//        System.out.println("type is " + Message.getMessageClass(msg.getMessageType()));
        out.writeInt(msg.getMessageType());
        //TODO provide more serialize chooses
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
//        System.out.println(out.toString(Charset.defaultCharset()));
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
//        System.out.println(message);
        out.add(message);
    }
}

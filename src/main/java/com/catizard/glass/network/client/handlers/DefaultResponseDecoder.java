package com.catizard.glass.network.client.handlers;

import com.catizard.glass.network.utils.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.Promise;
import java.util.List;
import java.util.Map;

public class DefaultResponseDecoder extends ByteToMessageDecoder {
    private Map<Integer, Promise<Object>> waitCh;

    public DefaultResponseDecoder(Map<Integer, Promise<Object>> waitCh) {
        this.waitCh = waitCh;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> outList) throws Exception {
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseMessage result = objectMapper.readValue(bytes, ResponseMessage.class);
            
        Promise<Object> targetPromise = waitCh.get(result.requestId);
        if (result.Err != null) {
            targetPromise.setFailure(result.Err);
        } else {
            targetPromise.setSuccess(result.responseResult);
        }
        waitCh.remove(result.requestId);
    }
}

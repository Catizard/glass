package com.catizard.glass.provider;

import com.catizard.glass.message.Message;
import com.catizard.glass.message.MessageCodec;
import com.catizard.glass.message.ServiceRegisterRequestMessage;
import com.catizard.glass.utils.Client;
import com.catizard.glass.utils.InetAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ServiceRegister extends Client {
    private InetAddress ip;

    public ServiceRegister(InetAddress ip) {
        this.ip = ip;
    }

    @Override
    public InetAddress getInetAddress() {
        return ip;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        System.out.println("[ServiceRegister] called child's initChannel()");
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 4, 4, 0, 0));
        ch.pipeline().addLast(new MessageCodec());
    }

    public void sendRegisterMessage(String serviceName, InetAddress serviceAddress) {
        ServiceRegisterRequestMessage message = new ServiceRegisterRequestMessage(serviceName, serviceAddress.getInetHost(), serviceAddress.getInetPort());
        //TODO: need to fetch response message
        super.sendMessage(message);
    }
}

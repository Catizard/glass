package ptp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ptp.Message.RPCRequestMessage;
import ptp.Message.RPCResponseMessage;
import ptp.services.HelloService;
import ptp.services.ServicesFactory;

import java.lang.reflect.Method;

public class RPCRequestMessageHandler extends SimpleChannelInboundHandler<RPCRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequestMessage msg) throws Exception {
        RPCResponseMessage rpcResponseMessage = new RPCResponseMessage();
        rpcResponseMessage.setRequestIdentify(msg.getRequestIdentify());
        try {
            HelloService services = (HelloService) ServicesFactory.getServices(Class.forName(msg.getInterfaceName()));
            Method method = services.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(services, msg.getParameterValues());
            System.out.println(invoke);
            rpcResponseMessage.setReturnValue(invoke);    
        } catch (Exception e) {
            e.printStackTrace();
            String err = e.getCause().getMessage();
            rpcResponseMessage.setExceptionValue(new Exception("remote error: " + err));
        }
        ctx.writeAndFlush(rpcResponseMessage);
    } 
}

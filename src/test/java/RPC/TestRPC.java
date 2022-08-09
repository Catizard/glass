package RPC;

import com.catizard.ptp.Message.RPCRequestMessage;
import com.catizard.ptp.services.HelloService;
import com.catizard.ptp.services.ServicesFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRPC {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        RPCRequestMessage message = new RPCRequestMessage(1
                , 1
                , "ptp.services.HelloService"
                , "hello"
                , String.class, new Class[]{String.class}
                , new Object[]{"world"});
        HelloService helloService = (HelloService) ServicesFactory.getServices(Class.forName(message.getInterfaceName()));
        Method method = helloService.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(helloService, message.getParameterValues());
        System.out.println(invoke);
    }
}

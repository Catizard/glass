package com.catizard.glass.network.client.interfaces;

import com.catizard.glass.network.utils.message.Message;
import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.Promise;

public interface Client {
    //TODO concretize exception type
    void connectTo(InetAddress ip) throws Exception;
    Object call(Message param) throws Exception;
    Object call(InetAddress ip, Message param) throws Exception;
    Promise<Object> callAsync(Message param) throws Exception;
    Promise<Object> callAsync(InetAddress ip, Message param) throws Exception;
}

package com.catizard.glass.network.client.interfaces;

import com.catizard.glass.utils.InetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.Promise;

public interface Client {
    //TODO concretize exception type
    void connectTo(InetAddress ip) throws Exception;
    Object call(Object param) throws Exception;
    Object call(InetAddress ip, Object param) throws Exception;
    Promise<Object> callAsync(Object param) throws Exception;
    Promise<Object> callAsync(InetAddress ip, Object param) throws Exception;
}

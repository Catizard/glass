package com.catizard.glass.network.client;
import com.catizard.glass.network.client.abstractClient.NettyClient;
import com.catizard.glass.network.utils.message.Message;
import com.catizard.glass.utils.InetAddress;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import java.util.HashMap;
import java.util.Map;

public class DefaultRunnableNettyClient extends NettyClient implements Runnable {
    private int requestId;
    private final Map<Integer, Promise<Object>> waitCh = new HashMap<>();
    
    @Override
    public Object call(Message param) throws Exception {
        Promise<Object> promise = callAsync(param);
        promise.await();
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new RuntimeException(promise.cause());
        }
    }
    
    @Override
    public Object call(InetAddress ip, Message param) throws Exception {
        //refresh the channel
        connectTo(ip);
        return call(param);
    }

    @Override
    public Promise<Object> callAsync(Message param) throws Exception {
        return doCallAsync(param);
    }
    
    @Override
    public Promise<Object> callAsync(InetAddress ip, Message param) throws Exception {
        connectTo(ip);
        return callAsync(param);
    }
    
    public Promise<Object> doCallAsync(Message param) throws Exception {
        Promise<Object> promise = new DefaultPromise<>(super.getWorker());
        waitCh.put(requestId, promise);
        super.sendMessage(param);
        return promise;
    }

    @Override
    public void run() {
        setupClient();
    }

    public int getRequestId() {
        ++requestId;
        return requestId;
    }
}

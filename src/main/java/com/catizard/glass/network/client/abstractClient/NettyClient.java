package com.catizard.glass.network.client.abstractClient;

import com.catizard.glass.network.client.interfaces.Client;
import io.netty.bootstrap.Bootstrap;

public abstract class NettyClient implements Client {
    public Bootstrap bootstrap;
}

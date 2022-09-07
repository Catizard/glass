package com.catizard.glass.grsc.abstracts;

import com.catizard.glass.grsc.interfaces.RegisterServiceCenter;
import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.network.server.interfaces.Server;
import com.catizard.glass.utils.InetAddress;

import java.util.List;

public abstract class AbstractSingleRegisterCenter implements RegisterServiceCenter {
    private Server server;
    private ServicesCollection services;

    public void setServer(Server server) {
        this.server = server;
    }

    public void setServices(ServicesCollection services) {
        this.services = services;
    }

    @Override
    public void listenTo(InetAddress ip) {
        server.listenTo(ip);
    }

    @Override
    public void submit(String serviceName, InetAddress address) {
        services.addService(serviceName, address);
    }

    @Override
    public List<InetAddress> fetch(String serviceName) {
        return services.getServices(serviceName);
    }

    @Override
    public void remove(String serviceName, InetAddress address) {
        services.removeService(serviceName, address);
    }
}

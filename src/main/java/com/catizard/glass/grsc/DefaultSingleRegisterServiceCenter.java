package com.catizard.glass.grsc;

import com.catizard.glass.grsc.abstracts.AbstractSingleRegisterCenter;
import com.catizard.glass.grsc.collection.MappedServices;
import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.network.server.DefaultNettyServer;
import com.catizard.glass.network.server.interfaces.Server;

import java.util.HashMap;

public class DefaultSingleRegisterServiceCenter extends AbstractSingleRegisterCenter {

    public DefaultSingleRegisterServiceCenter() {
        super(new DefaultNettyServer(), MappedServices.initMappedServices(new HashMap<>()));
    }
}

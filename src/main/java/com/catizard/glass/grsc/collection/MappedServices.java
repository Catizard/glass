package com.catizard.glass.grsc.collection;

import com.catizard.glass.grsc.interfaces.ServicesCollection;
import com.catizard.glass.utils.InetAddress;

import java.util.ArrayList;
import java.util.Map;

public class MappedServices implements ServicesCollection {
    private final Map<String, ArrayList<InetAddress>> services;
    
    private MappedServices(Map<String, ArrayList<InetAddress>> map) {
        this.services = map;
    }
    
    @Override
    public boolean addService(String serviceName, InetAddress address) {
        ArrayList<InetAddress> list = services.get(serviceName);
        if(list == null) {
            list = new ArrayList<>();
            list.add(address);
            services.put(serviceName, list);
            return true;
        } else {
            boolean ok = true;
            for (InetAddress inetAddress : list) {
                if (inetAddress.equals(address)) {
                    ok = false;
                    break;
                }
            }
            if(ok) {
                list.add(address);
            }
            return ok;
        }
    }

    @Override
    public boolean removeService(String serviceName, InetAddress address) {
        ArrayList<InetAddress> list = services.get(serviceName);
        if (list == null) {
            return false;
        }
        return list.remove(address);
    }

    @Override
    public ArrayList<InetAddress> getServices(String serviceName) {
        return services.get(serviceName);
    }

    public static MappedServices initMappedServices(Map<String, ArrayList<InetAddress>> map) {
        return new MappedServices(map);
    }
}

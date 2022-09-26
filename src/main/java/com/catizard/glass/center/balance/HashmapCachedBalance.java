package com.catizard.glass.center.balance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HashmapCachedBalance extends AbstractCachedBalance {
    Map<String, List<String>> cache = new HashMap<>();

    @Override
    public void addCache(String serviceName, List<String> list) {
        cache.put(serviceName, list);
    }

    @Override
    public void removeCache(String serviceName) {
        //TODO maybe there exists concurrent problem
        cache.remove(serviceName);
    }
}

package com.catizard.glass.center.balance;

import java.util.List;

public abstract class AbstractCachedBalance implements Balance {
    public abstract void addCache(String serviceName, List<String> list);
    public abstract void removeCache(String serviceName);
}

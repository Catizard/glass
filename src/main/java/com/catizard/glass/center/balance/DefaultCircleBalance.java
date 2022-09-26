package com.catizard.glass.center.balance;

import java.util.List;

public class DefaultCircleBalance extends HashmapCachedBalance {
    private int targetIndex = 0;
    private static final Object LOCK = new Object();
    @Override
    public String processBeforeProvide(String data) {
        //there is no need to add meta info onto data
        //so just return raw data back
        return data;
    }

    @Override
    public void processAfterProvide() {

    }

    @Override
    public void processBeforeFetch() {

    }

    @Override
    public String processAfterFetch(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        
        synchronized (LOCK) {
            targetIndex++;
            if (targetIndex > 1e9) {
                targetIndex = 0;
            }
            return list.get(targetIndex % list.size());
        }
    }
}

package com.catizard.glass.center.balance;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

public class DefaultCircleBalance extends HashmapCachedBalance {
    private int targetIndex = 0;
    private static final Object LOCK = new Object();
    private CuratorFramework zkCli = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));

    public DefaultCircleBalance() {
        zkCli.start();
    }

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
    public String processAfterFetch(String parentPath, List<String> rawList) {
        if (rawList == null || rawList.isEmpty()) {
            return null;
        }
        
        synchronized (LOCK) {
            targetIndex++;
            if (targetIndex > 1e9) {
                targetIndex = 0;
            }
            String serverNodeName = rawList.get(targetIndex % rawList.size());
            try {
                return new String(zkCli.getData().forPath(parentPath + "/" + serverNodeName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

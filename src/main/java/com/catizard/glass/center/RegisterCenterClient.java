package com.catizard.glass.center;

import com.catizard.glass.center.balance.Balance;
import com.catizard.glass.utils.InetAddress;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class RegisterCenterClient {
    Balance balance;
    CuratorFramework zkCli = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
    
    public RegisterCenterClient(Balance balance) {
        this.balance = balance;
        zkCli.start();
    }
    
    public void provideService(String serviceName, String data) {
        String wrappedData = balance.processBeforeProvide(data);
        //send to zk        
        balance.processAfterProvide();
    }
    
    public String fetchService(String serviceName) {
        balance.processBeforeFetch();
        //fetch data
        List<String> list = new ArrayList<>();
        return balance.processAfterFetch(list);
    }
}

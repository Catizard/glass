package com.catizard.glass.center;

import com.catizard.glass.center.utils.balance.Balance;
import com.catizard.glass.center.utils.wrappers.ServicePathFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RegisterCenterClient {
    private Balance balance;
    private CuratorFramework zkCli = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
    private ServicePathFactory pathFactory;
    public RegisterCenterClient(Balance balance, ServicePathFactory pathFactory) {
        this.balance = balance;
        this.pathFactory = pathFactory;
        zkCli.start();
    }
    
    public void provideService(String serviceName, String data) {
        String wrappedData = balance.processBeforeProvide(data);
        String servicePath = pathFactory.ServiceNameToServicePath(serviceName);
        System.out.println(servicePath);

        try {
            System.out.println(wrappedData);
            zkCli.create().creatingParentsIfNeeded().
                    withMode(CreateMode.EPHEMERAL_SEQUENTIAL).
                    forPath(servicePath, wrappedData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        balance.processAfterProvide();
    }
    
    public String fetchService(String serviceName) {
        balance.processBeforeFetch();
        //fetch data
        List<String> rawList = null;
        try {
            String servicePath = pathFactory.ServiceNameToServicePath(serviceName);
            String parentPath = servicePath.substring(0, servicePath.lastIndexOf("/"));
            rawList = zkCli.getChildren().forPath(parentPath);
            return balance.processAfterFetch(parentPath, rawList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

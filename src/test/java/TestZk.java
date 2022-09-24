import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.Thread.sleep;

public class TestZk {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        client.start();
        
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/com/catizard/services/HelloService/server", "192.168.109.128:8080".getBytes(StandardCharsets.UTF_8));
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/com/catizard/services/HelloService/server", "192.168.109.128:8081".getBytes(StandardCharsets.UTF_8));
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/com/catizard/services/HelloService/server", "192.168.109.128:8082".getBytes(StandardCharsets.UTF_8));

        List<String> servers = client.getChildren().forPath("/com/catizard/services/HelloService");
        byte[] bytes = client.getData().forPath("/com/catizard/services/HelloService/" + servers.get(0));
        String res = new String(bytes);
        System.out.println(res);
        System.out.println(servers);
        while (true) {
            sleep(5000);
        }
    }
}

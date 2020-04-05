package com.example.demo.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.demo.config.SpringConfig;
import com.example.demo.config.ZkBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.util.StringUtils;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class DataPrepare extends ZkBase implements Watcher {

    CountDownLatch countDownLatch;

    public static volatile Properties env = new Properties();

    private CuratorFramework client;

    public DataPrepare (CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        init();
    }

    public void init() {
        client = ZkBase.getClient();

        try {
            byte[] bytes = client.getData().usingWatcher(this).forPath("/datasource");
            resolveConfig(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();

        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged){

            try {
                resolveConfig(client.getData().usingWatcher(this).forPath("/datasource"));
                SpringConfig.refreshDatasource();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    DruidDataSource resolveConfig (byte[] originalByte) {

        String data = new String(originalByte);
        if (StringUtils.isEmpty(data)) {
            return null;
        }

        String[] config = data.split(";");
        for (String line : config) {
            String[] entry = StringUtils.split(line,"=");
            env.put(entry[0].trim(),entry[1].trim());
        }
        return null;
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        new Thread(() -> init()).start();
//
//    }


}

package com.example.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.demo.datasource.DataPrepare;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

@Component
@Configuration
public class SpringConfig implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Primary
    @Bean
//    @Scope("prototype")
    public DataSource dataSource () {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            new DataPrepare(countDownLatch).init();
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return generateDatasource();
    }

    public static void refreshDatasource () {
        DruidDataSource dataSource = (DruidDataSource) generateDatasource();

        DruidDataSource dataSourceBean = (DruidDataSource) applicationContext.getBean("dataSource",DataSource.class);
//        dataSourceBean.close();

        dataSourceBean.setUrl(dataSource.getUrl());
        dataSourceBean.setUsername(dataSource.getUsername());
        dataSourceBean.setPassword(dataSource.getPassword());
        dataSourceBean.setDriverClassName(dataSource.getDriverClassName());

        try {
            dataSourceBean.restart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DataSource generateDatasource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(DataPrepare.env.getProperty("jdbc.driver"));
        druidDataSource.setUrl(DataPrepare.env.getProperty("jdbc.url"));
        druidDataSource.setUsername(DataPrepare.env.getProperty("jdbc.username"));
        druidDataSource.setPassword(DataPrepare.env.getProperty("jdbc.password"));
        return druidDataSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

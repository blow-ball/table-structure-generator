package com.geqian.structure.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author geqian
 * @date 12:49 2023/7/12
 */
@Configuration
public class DatabaseManagerConfig {

    @Bean
    @ConfigurationProperties(prefix = "database.info.db2")
    public DatabaseManager db2(){
        return new DatabaseManager();
    }


    @Bean
    @ConfigurationProperties(prefix = "database.info.mysql")
    public DatabaseManager mysql(){
        return new DatabaseManager();
    }


    @Bean
    @ConfigurationProperties(prefix = "database.info.oracle")
    public DatabaseManager oracle(){
        return new DatabaseManager();
    }
}

package com.caipiao.config;

import lombok.Data;

@Data
public class HikariConfig {

    private String poolName;

    private boolean autoCommit;

    private long connectionTimeout;

    private long idleTimeout;

    private long maxLifetime;

    private int maximumPoolSize;

    private int minimumIdle;

    private String connectionTestQuery;
}

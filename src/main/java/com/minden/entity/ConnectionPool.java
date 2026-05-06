package com.minden.entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.sql.DataSource;

public class ConnectionPool {

    private final BlockingQueue<Connection> pool;
    private final DataSource dataSource;

    public ConnectionPool(int poolSize) throws SQLException {
        this.dataSource = DatabaseConfig.createDataSource();
        this.pool = new ArrayBlockingQueue<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            pool.add(dataSource.getConnection());
        }
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }
}

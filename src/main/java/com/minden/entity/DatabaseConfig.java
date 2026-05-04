package com.minden.entity;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

public class DatabaseConfig {

    public static DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        // Важливо: вказуйте назву файлу бази, наприклад ./data/game_db
        ds.setURL("jdbc:h2:./data/game_db;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    public static void runMigrations(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            // Якщо ви запускаєте вперше і Flyway бачить файли V1, V2
            .load();

        var result = flyway.migrate();
        System.out.printf(
            "Flyway: applied %d migrations, current version: %s%n",
            result.migrationsExecuted,
            result.targetSchemaVersion
        );
    }

    public static DataSource initialize() {
        DataSource ds = createDataSource();
        runMigrations(ds);
        return ds;
    }
}
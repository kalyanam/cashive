package com.cashive.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created by mkalyan on 8/22/16.
 */
public class DBMigrator {
    private static final Logger logger = LoggerFactory.getLogger(DBMigrator.class);

    public void migrate() {
        logger.info("Starting the db migration");
        Flyway flyway = new Flyway();
        flyway.setDataSource(createDataSource("org.postgresql.Driver",System.getenv("JDBC_DATABASE_URL")));
        flyway.migrate();
        logger.info("Finished db migration");
    }

    private DataSource createDataSource(String driverName, String dbUrl) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(dbUrl);

        return dataSource;
    }
}

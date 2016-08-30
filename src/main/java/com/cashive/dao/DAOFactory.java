package com.cashive.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by mkalyan on 8/26/16.
 */
public class DAOFactory {
    private JdbcTemplate jdbcTemplate;

    private DAOFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AccountsDAO getAccountsDAO() {
        return new AccountsDAO(jdbcTemplate);
    }

    public GroupsDAO getGroupsDAO() {
        return new GroupsDAO(jdbcTemplate, getAccountsDAO());
    }

    public InvitesDAO getInvitesDAO() {
        return new InvitesDAO(jdbcTemplate, getAccountsDAO(), getGroupsDAO());
    }

    public ActiveGroupInfoDAO getActiveGroupInfoDAO() {
        return new ActiveGroupInfoDAO(jdbcTemplate, getGroupsDAO(), getAccountsDAO());
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String driverName;
        private String dbUrl;

        public Builder withDriverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public Builder withDbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
            return this;
        }

        private DataSource createDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(this.driverName);
            dataSource.setUrl(this.dbUrl);

            return dataSource;
        }

        public DAOFactory build() {
            JdbcTemplate template = new JdbcTemplate(createDataSource());
            return new DAOFactory(template);
        }
    }
}

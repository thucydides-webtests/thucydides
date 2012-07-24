package net.thucydides.core.jpa;

import com.google.inject.Inject;
import net.thucydides.core.jpa.JPAProviderConfig;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Rahul
 * Date: 6/7/12
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateEnvironmentVariablesConfig extends AbstractJPAProviderConfig {



    private final EnvironmentVariables environmentVariables;
    private final LocalDatabase localDatabase;

    private static final JPAProvider PROVIDER = JPAProvider.Hibernate;

    @Inject
    public HibernateEnvironmentVariablesConfig(EnvironmentVariables environmentVariables,
                                              LocalDatabase localDatabase) {
        this.environmentVariables = environmentVariables;
        this.localDatabase = localDatabase;
    }

    @Override
    public void setProperties(Properties properties) {

        String driver = environmentVariables.getProperty("thucydides.statistics.driver_class", localDatabase.getDriver());
        String url = environmentVariables.getProperty("thucydides.statistics.url", localDatabase.getUrl());
        String username = environmentVariables.getProperty("thucydides.statistics.username", localDatabase.getUsername());
        String password = environmentVariables.getProperty("thucydides.statistics.password", localDatabase.getPassword());
        String dialect = environmentVariables.getProperty("thucydides.statistics.dialect", localDatabase.getDialect());


        properties.put("hibernate.connection.driver_class", driver);
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.connection.pool_size", "1");

        boolean databaseIsConfigured = databaseIsConfigured(properties);
        if (isUsingLocalDatabase() || !databaseIsConfigured) {
            properties.put("hibernate.hbm2ddl.auto", "update");
        } else {
            properties.put("hibernate.hbm2ddl.auto", "validate");
        }

    }

    private boolean databaseIsConfigured(Properties targetConfiguration) {
        Properties connectionProps = new Properties();
        connectionProps.put("user", targetConfiguration.getProperty("hibernate.connection.username"));
        connectionProps.put("password", targetConfiguration.getProperty("hibernate.connection.password"));
        String jdbcConnection = targetConfiguration.getProperty("hibernate.connection.url");
        try {
            Connection conn = DriverManager.getConnection(jdbcConnection, connectionProps);
            List<String> tables = getTablesFrom(conn);
            return tables.contains("TESTRUN");
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean isUsingLocalDatabase() {
        return isUsingLocalDatabase(environmentVariables);
    }

    @Override
    public JPAProvider getProvider() {
        return  PROVIDER;
    }


}

package net.thucydides.core.jpa;

import com.google.inject.Inject;
import net.thucydides.core.jpa.JPAProviderConfig;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;
import org.eclipse.persistence.config.TargetDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Rahul
 * Date: 6/13/12
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class EclipseLinkEnvironmentVariablesConfig extends AbstractJPAProviderConfig {

    private final EnvironmentVariables environmentVariables;
    private final LocalDatabase localDatabase;

    private static final JPAProvider PROVIDER = JPAProvider.EclipseLink;


    @Inject
    public EclipseLinkEnvironmentVariablesConfig(EnvironmentVariables environmentVariables,
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
        String dialect = environmentVariables.getProperty("thucydides.statistics.dialect", TargetDatabase.Auto);

        properties.put("javax.persistence.jdbc.driver", driver);
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        properties.put("eclipselink.target-database", dialect);
        properties.put("eclipselink.connection-pool.default.initial", "1");
        properties.put("eclipselink.connection-pool.default.max", "10");

        boolean databaseIsConfigured = databaseIsConfigured(properties);
        if (isUsingLocalDatabase() || !databaseIsConfigured) {
            properties.put("eclipselink.ddl-generation", "create-or-extend-tables");
        } else {
            properties.put("eclipselink.ddl-generation", "none");
        }

    }

    private boolean databaseIsConfigured(Properties targetConfiguration) {
        Properties connectionProps = new Properties();
        connectionProps.put("user", targetConfiguration.getProperty("javax.persistence.jdbc.user"));
        connectionProps.put("password", targetConfiguration.getProperty("javax.persistence.jdbc.password"));
        String jdbcConnection = targetConfiguration.getProperty("javax.persistence.jdbc.url");
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

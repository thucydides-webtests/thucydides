package net.thucydides.core.guice;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Obtain the statistics database connection details.
 * The statistics database is configured using the environment variables.
 * If the default database is not used, the database must already exist.
 */
public class EnvironmentVariablesDatabaseConfig implements DatabaseConfig {

    private static final int TABLE_NAME_COLUMN  = 3;

    private final EnvironmentVariables environmentVariables;
    private final LocalDatabase localDatabase;
    private boolean isActive = true;

    @Inject
    public EnvironmentVariablesDatabaseConfig(EnvironmentVariables environmentVariables,
                                              LocalDatabase localDatabase) {
        this.environmentVariables = environmentVariables;
        this.localDatabase = localDatabase;
    }

    public Properties getConnectionProperties() {
        Properties properties = new Properties();

        String driver = ThucydidesSystemProperty.STATISTICS_DRIVER.from(environmentVariables, localDatabase.getDriver());
        String url = ThucydidesSystemProperty.STATISTICS_URL.from(environmentVariables, localDatabase.getUrl());
        String username = ThucydidesSystemProperty.STATISTICS_USERNAME.from(environmentVariables, localDatabase.getUsername());
        String password = ThucydidesSystemProperty.STATISTICS_PASSWORD.from(environmentVariables, localDatabase.getPassword());
        String dialect = ThucydidesSystemProperty.STATISTICS_DIALECT.from(environmentVariables, localDatabase.getDialect());

        properties.put("hibernate.connection.driver_class", driver);
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.connection.pool_size", "1");
        return properties;
    }

    public Properties getProperties() {
        Properties properties = getConnectionProperties();

        if (isActive()) {
            configureDatabaseInitialisation(properties);
        }
        return properties;
    }

    private void configureDatabaseInitialisation(Properties properties) {
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

    private List<String> getTablesFrom(Connection conn) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        List<String> tableNames = new ArrayList<String>();
        while (rs.next()) {
            tableNames.add(rs.getString(TABLE_NAME_COLUMN));
        }
        return tableNames;
    }

    public boolean isUsingLocalDatabase() {
        return (environmentVariables.getProperty("thucydides.statistics.url") == null);
    }

    private boolean isStatisticsDisabled() {
        return (!Boolean.valueOf(environmentVariables.getProperty(ThucydidesSystemProperty.RECORD_STATISTICS, "true")));
    }

    public void disable() {
        isActive = false;
    }

    public void enable() {
        isActive = true;
    }

    public boolean isActive() {
        return isActive && !isStatisticsDisabled();
    }
}

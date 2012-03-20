package net.thucydides.core.guice;

import net.thucydides.core.util.EnvironmentVariables;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Obtain the statistics database connection details.
 * The statistics database is configured using the environment variables.
 * If the default HSQLDB database is not used, the database must already exist.
 */
public class DatabaseConfig {

    private static final int TABLE_NAME_COLUMN  = 3;

    private final EnvironmentVariables environmentVariables;

    public DatabaseConfig(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static DatabaseConfig usingPropertiesFrom(final EnvironmentVariables environmentVariables) {
        return new DatabaseConfig(environmentVariables);
    }

    public Properties getProperties() {
        Properties properties = new Properties();

        String driver = environmentVariables.getProperty("thucydides.statistics.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        String url = environmentVariables.getProperty("thucydides.statistics.url", getDefaultDatabaseUrl());
        String username = environmentVariables.getProperty("thucydides.statistics.username", "sa");
        String password = environmentVariables.getProperty("thucydides.statistics.password", "");
        String dialect = environmentVariables.getProperty("thucydides.statistics.dialect", "org.hibernate.dialect.HSQLDialect");

        properties.put("hibernate.connection.driver_class", driver);
        properties.put("hibernate.connection.url", url);
        properties.put("hibernate.connection.username", username);
        properties.put("hibernate.connection.password", password);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.connection.pool_size", "1");

        boolean databaseIsConfigured = databaseIsConfigured(properties);
        if (usingDefaultDatabase() || !databaseIsConfigured) {
            properties.put("hibernate.hbm2ddl.auto", "update");
        } else if (databaseIsConfigured) {
            properties.put("hibernate.hbm2ddl.auto", "validate");
        }
        return properties;
    }

    private String getDefaultDatabaseUrl() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String defaultDatabase = defaultThucydidesDirectory + "/stats";
        return "jdbc:hsqldb:file:" + defaultDatabase + ";shutdown=true";
    }

    private boolean usingDefaultDatabase() {
        return (environmentVariables.getProperty("thucydides.statistics.url") == null);
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

}

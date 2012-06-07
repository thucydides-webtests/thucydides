package net.thucydides.core.guice;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

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
 * If the default database is not used, the database must already exist.
 */
public class EnvironmentVariablesDatabaseConfig implements DatabaseConfig {

    private static final int TABLE_NAME_COLUMN  = 3;

    private final EnvironmentVariables environmentVariables;
    private final LocalDatabase localDatabase;
    private final JPAProviderConfig providerConfig;
    private boolean isActive = true;

    @Inject
    public EnvironmentVariablesDatabaseConfig(EnvironmentVariables environmentVariables,
                                              LocalDatabase localDatabase) {
        this.environmentVariables = environmentVariables;
        this.localDatabase = localDatabase;
        this.providerConfig = new HibernateEnvironmentVariablesConfig(environmentVariables, localDatabase);
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        String driver = environmentVariables.getProperty("thucydides.statistics.driver_class", localDatabase.getDriver());
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + driver);
        providerConfig.setProperties(properties);

        System.out.println("##################################3" + properties);

        return properties;
    }

    public boolean isUsingLocalDatabase() {
        return (environmentVariables.getProperty("thucydides.statistics.url") == null);
    }

    private boolean isStatisticsDisabled() {
        return (Boolean.valueOf(environmentVariables.getProperty(ThucydidesSystemProperty.RECORD_STATISTICS, "true")));
    }

    @Override
    public void disable() {
        isActive = false;
    }

    @Override
    public void enable() {
        isActive = true;
    }

    @Override
    public boolean isActive() {
        return isActive && !isStatisticsDisabled();
    }
}

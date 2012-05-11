package net.thucydides.core.statistics.database;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

public class LocalHSQLDBDatabase implements LocalDatabase {

    private final EnvironmentVariables environmentVariables;

    @Inject
    public LocalHSQLDBDatabase(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean isAvailable() {
        return true;
    }

    public String getUrl() {
        return "jdbc:hsqldb:" + getDatabasePath() + ";shutdown=true";
    }

    public String getDriver() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

    public String getUsername() {
        return "sa";
    }

    public String getPassword() {
        return "";
    }

    public String getDialect() {
        return "org.hibernate.dialect.HSQLDialect";
    }

    public String getDatabaseName() {
        String projectKey = ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, "default");
        return StringUtils.join(ImmutableList.of("stats", projectKey), "-");
    }

    public String getDatabasePath() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String thucydidesHomeDirectory = ThucydidesSystemProperty.THUCYDIDES_HOME.from(environmentVariables, defaultThucydidesDirectory);
        return thucydidesHomeDirectory + "/" + getDatabaseName();
    }
}

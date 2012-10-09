package net.thucydides.core.statistics.database;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.sql.SQLException;

public class LocalH2ServerDatabase implements LocalDatabase {

    private final EnvironmentVariables environmentVariables;

    @Inject
    public LocalH2ServerDatabase(EnvironmentVariables environmentVariables) {
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
        return "jdbc:h2:/" + getDatabasePath() + ";AUTO_SERVER=TRUE";
//        return "jdbc:h2:/" + getDatabasePath();
    }

    public String getDriver() {
        return "org.h2.Driver";
    }

    public String getUsername() {
        return "SA";
    }

    public String getPassword() {
        return "";
    }

    public String getDialect() {
        return "org.hibernate.dialect.H2Dialect";
    }

    public String getDatabaseName() {
        String projectKey = ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, Thucydides.getDefaultProjectKey());
        return StringUtils.join(ImmutableList.of("stats", projectKey), "-");
    }

    public String getDatabasePath() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String thucydidesHomeDirectory = ThucydidesSystemProperty.THUCYDIDES_HOME.from(environmentVariables, defaultThucydidesDirectory);
        return thucydidesHomeDirectory + "/" + getDatabaseName();
    }
}

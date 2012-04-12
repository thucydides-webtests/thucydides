package net.thucydides.core.statistics.integration.db;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.hsqldb.HsqlException;
import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LocalFileBasedHsqldbDatabase implements LocalDatabase {

    private final EnvironmentVariables environmentVariables;

    @Inject
    public LocalFileBasedHsqldbDatabase(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getUrl() {
        return "jdbc:hsqldb:file:" + getDatabasePath() + ";shutdown=true";
    }

    @Override
    public String getDriver() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

    @Override
    public String getUsername() {
        return "SA";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getDialect() {
        return "org.hibernate.dialect.HSQLDialect";
    }

    public String getDatabaseName() {
        String projectKey = ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, "default");
        System.out.println("USING PROJECT KEY: " + projectKey);
        return StringUtils.join(ImmutableList.of("stats", projectKey), "-");
    }

    public String getDatabasePath() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String thucydidesHomeDirectory = ThucydidesSystemProperty.THUCYDIDES_HOME.from(environmentVariables, defaultThucydidesDirectory);
        System.out.println("USING DATABASE: " + getDatabaseName());
        return thucydidesHomeDirectory + "/" + getDatabaseName();
    }
}

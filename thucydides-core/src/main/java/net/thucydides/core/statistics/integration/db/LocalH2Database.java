package net.thucydides.core.statistics.integration.db;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;

import java.sql.SQLException;

public class LocalH2Database implements LocalDatabase {

    private final EnvironmentVariables environmentVariables;

    Server server;

    @Inject
    public LocalH2Database(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public void start() {
        try {
            server = Server.createTcpServer( new String[] { "-tcpAllowOthers" }).start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public boolean isAvailable() {
        return (server != null) && (server.isRunning(false));
    }

    @Override
    public String getUrl() {
        return "jdbc:h2:tcp://localhost" + getDatabasePath();
    }

    @Override
    public String getDriver() {
        return "org.h2.Driver";
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
        return "org.hibernate.dialect.H2Dialect";
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

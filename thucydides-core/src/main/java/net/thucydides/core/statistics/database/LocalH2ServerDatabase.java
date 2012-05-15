package net.thucydides.core.statistics.database;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
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

    Server server;

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalH2ServerDatabase.class);
    private int port;

    @Inject
    public LocalH2ServerDatabase(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void start() {
        try {
            findFreePort();
            LOGGER.info("STARTING H2 DATABASE AT " + getUrl());
            server = Server.createTcpServer(new String[]{"-tcpAllowOthers", "-tcpPort", Integer.toString(getPort())}).start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void findFreePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (Exception e) {
            port = -1;
        }
    }

    public void stop() {
        LOGGER.info("SHUTTING DOWN H2 DATABASE");
        server.shutdown();
        server.stop();
    }

    public boolean isAvailable() {
        return (server != null) && (server.isRunning(false));
    }

    public String getUrl() {
        return "jdbc:h2:tcp://localhost" + ":" + getPort() + "/" + getDatabasePath();
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
        String projectKey = ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, "default");
        return StringUtils.join(ImmutableList.of("stats", projectKey), "-");
    }

    public String getDatabasePath() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String thucydidesHomeDirectory = ThucydidesSystemProperty.THUCYDIDES_HOME.from(environmentVariables, defaultThucydidesDirectory);
        return thucydidesHomeDirectory + "/" + getDatabaseName();
    }

    public int getPort() {
        return port;
    }
}

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
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LocalHSqldbDatabase implements LocalDatabase {

    private static final String LOCALHOST = "127.0.0.1";
    private final EnvironmentVariables environmentVariables;
    private Server server;

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalHSqldbDatabase.class);

    @Inject
    public LocalHSqldbDatabase(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public void start() {
        startServer();
    }

    @Override
    public void stop() {
        stopServer();
    }
    
    @Override
    public String getUrl() {
        return "jdbc:hsqldb:hsql://" + LOCALHOST + ":" + getPort() + "/" + getDatabaseName();
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

    private synchronized void startServer() {
        LOGGER.info("Starting local statistics database");
        server = new Server();
        server.setAddress(LOCALHOST);
        server.setDatabaseName(0, getDatabaseName());
        server.setDatabasePath(0, "file:" + getDatabasePath());
        server.setPort(getPort());
        server.setTrace(false);
        server.setLogWriter(null);
        server.setDaemon(true);
        server.start();
    }

    private synchronized void stopServer() {
        if (isAvailable()) {
            shutdownDatabase();
            server.stop();
        }
    }

    private void shutdownDatabase() {
        if (isAvailable()) {
            LOGGER.info("Shutting down local statistics database");
            try {
                Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                connection.createStatement().executeUpdate("SHUTDOWN");
            } catch (SQLException e) {
                LOGGER.warn("Could not shut down local database");
            }
        }
    }

    public String getDatabaseName() {
        String projectKey = ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables, "default");
        return StringUtils.join(ImmutableList.of("stats", projectKey), "-");
    }

    public String getDatabasePath() {
        String defaultThucydidesDirectory = environmentVariables.getProperty("user.home") + "/.thucydides";
        String thucydidesHomeDirectory = ThucydidesSystemProperty.THUCYDIDES_HOME.from(environmentVariables,
                defaultThucydidesDirectory);
        return thucydidesHomeDirectory + "/hsqldb";
    }

    public int getPort() {
        return Integer.parseInt(ThucydidesSystemProperty.THUCYDIDES_PORT.from(environmentVariables,
                Integer.toString(ThucydidesSystemProperty.DEFAULT_DATABASE_PORT)));
    }


    private boolean isRunningLocally() {
        boolean isRunning = false;
        if (server != null) {
            try {
                server.checkRunning(true);
                isRunning = true;
            } catch (HsqlException e) {
                isRunning = false;
            }
        }
        return isRunning;
    }

    @Override
    public boolean isAvailable() {
        return isRunningLocally() || isRunningElsewhere();
    }

    private boolean isRunningElsewhere() {
        Socket socket = null;
        boolean available = true;
        try {
            socket = new Socket(InetAddress.getByName(LOCALHOST), getPort());
        } catch (IOException e) {
            available = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }

        return available;
    }
}

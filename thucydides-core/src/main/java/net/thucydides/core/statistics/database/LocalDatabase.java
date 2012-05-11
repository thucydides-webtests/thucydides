package net.thucydides.core.statistics.database;

/**
 * A local database used to store statistics by default.
 */
public interface LocalDatabase {
    void start();
    void stop();
    boolean isAvailable();

    String getUrl();
    String getDriver();
    String getUsername();
    String getPassword();
    String getDialect();
}

package net.thucydides.core.statistics;

import net.thucydides.core.guice.DatabaseConfig;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenConfiguringTheStatisticsDatabase {

    EnvironmentVariables environmentVariables;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
    }
    
    @Test
    public void should_define_an_hsqldb_database_by_default() {
        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.connection.driver_class"), is("org.hsqldb.jdbc.JDBCDriver"));
    }


    @Test
    public void should_define_a_local_hsqldb_database_by_default() {
        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.connection.url"), containsString("jdbc:hsqldb:file:"));
        assertThat(properties.getProperty("hibernate.connection.url"), containsString("stats"));
        assertThat(properties.getProperty("hibernate.connection.url"), containsString(";shutdown=true"));
    }


    @Test
    public void should_update_the_default_local_database_automatically() {
        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.hbm2ddl.auto"), is("update"));
    }

    @Test
    public void should_validate_but_not_update_an_existing_custom_database() throws SQLException {
        String preexistingDatabaseUrl = "jdbc:hsqldb:mem:stats";
        createPreexistingDatabaseFor(preexistingDatabaseUrl);

        environmentVariables.setProperty("thucydides.statistics.url",preexistingDatabaseUrl);

        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.hbm2ddl.auto"), is("validate"));
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void should_update_an_empty_custom_database() throws IOException, SQLException {
        String emptyDatabaseUrl = "jdbc:hsqldb:mem:empty";
        createEmptyDatabaseFor(emptyDatabaseUrl);

        environmentVariables.setProperty("thucydides.statistics.url",emptyDatabaseUrl);

        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.hbm2ddl.auto"), is("update"));
    }

    @Test
    public void should_be_able_to_override_the_jdbc_driver_using_the_environment_variables() {

        environmentVariables.setProperty("thucydides.statistics.driver_class","org.postgresql.Driver");
        environmentVariables.setProperty("thucydides.statistics.url","jdbc:postgresql:dbserver/stats");
        environmentVariables.setProperty("thucydides.statistics.username","admin");
        environmentVariables.setProperty("thucydides.statistics.password","password");
        environmentVariables.setProperty("thucydides.statistics.dialect","org.hibernate.dialect.PostgresDialect");

        Properties properties = DatabaseConfig.usingPropertiesFrom(environmentVariables).getProperties();

        assertThat(properties.getProperty("hibernate.connection.driver_class"), is("org.postgresql.Driver"));
        assertThat(properties.getProperty("hibernate.connection.url"), is("jdbc:postgresql:dbserver/stats"));
        assertThat(properties.getProperty("hibernate.connection.username"), is("admin"));
        assertThat(properties.getProperty("hibernate.connection.password"), is("password"));
        assertThat(properties.getProperty("hibernate.dialect"), is("org.hibernate.dialect.PostgresDialect"));
    }

    private void createEmptyDatabaseFor(String emptyDatabaseUrl) throws SQLException {
        DriverManager.getConnection(emptyDatabaseUrl, "SA", "");
    }

    private void createPreexistingDatabaseFor(String preexistingDatabaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(preexistingDatabaseUrl, "SA", "");
        connection.prepareStatement("CREATE MEMORY TABLE PUBLIC.TESTRUN(ID BIGINT NOT NULL PRIMARY KEY,DURATION BIGINT NOT NULL,EXECUTIONDATE TIMESTAMP,RESULT INTEGER,TITLE VARCHAR(255))")
                  .executeUpdate();
    }
}

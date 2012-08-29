package net.thucydides.core.statistics;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.EnvironmentVariablesDatabaseConfig;
import net.thucydides.core.jpa.JPAProvider;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.statistics.database.LocalH2ServerDatabase;
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

public class WhenConfiguringTheStatisticsDatabaseWithOpenJPA {

    EnvironmentVariables environmentVariables;
    EnvironmentVariablesDatabaseConfig databaseConfig;

    LocalDatabase localDatabase;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.OpenJPA.name());
        localDatabase = new LocalH2ServerDatabase(environmentVariables);
        databaseConfig = new EnvironmentVariablesDatabaseConfig(environmentVariables, localDatabase);
    }

    @Test
    public void should_define_an_h2_database_by_default() {
        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("javax.persistence.jdbc.driver"), is("org.h2.Driver"));
    }


    @Test
    public void should_define_a_local_hsqldb_database_by_default() {
        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("jdbc:"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("stats-thucydides"));
    }

    @Test
    public void should_define_a_local_hsqldb_database_using_the_project_key_if_provided() {
        environmentVariables.setProperty("thucydides.project.key","myproject");
        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("jdbc:"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("stats-myproject"));
    }

    @Test
    public void should_define_a_local_hsqldb_database_using_the_full_database_name_if_provided() {
        environmentVariables.setProperty("thucydides.project.key","myproject");
        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("jdbc:"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), containsString("stats-myproject"));
    }

    @Test
    public void should_update_the_default_local_database_automatically() {
        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("openjpa.jdbc.SynchronizeMappings"), is("buildSchema"));
    }

    @Test
    public void should_validate_but_not_update_an_existing_custom_database() throws SQLException, ClassNotFoundException {
        String preexistingDatabaseUrl = "jdbc:hsqldb:mem:existing-database-openJPA";
        Class.forName("org.hsqldb.jdbcDriver");
        deletePreexistingDatabaseFor(preexistingDatabaseUrl);
        createPreexistingDatabaseFor(preexistingDatabaseUrl);

        environmentVariables.setProperty("thucydides.statistics.url",preexistingDatabaseUrl);

        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("openjpa.jdbc.SynchronizeMappings"), is("validate"));

    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void should_update_an_empty_custom_database() throws IOException, SQLException {
        String emptyDatabaseUrl = "jdbc:hsqldb:mem:empty";
        createEmptyDatabaseFor(emptyDatabaseUrl);

        environmentVariables.setProperty("thucydides.statistics.url",emptyDatabaseUrl);

        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("openjpa.jdbc.SynchronizeMappings"), is("buildSchema"));
    }

    @Test
    public void should_be_able_to_override_the_jdbc_driver_using_the_environment_variables() {

        environmentVariables.setProperty("thucydides.statistics.driver_class","org.postgresql.Driver");
        environmentVariables.setProperty("thucydides.statistics.url","jdbc:postgresql:dbserver/stats");
        environmentVariables.setProperty("thucydides.statistics.username","admin");
        environmentVariables.setProperty("thucydides.statistics.password","password");
        environmentVariables.setProperty("thucydides.statistics.dialect","org.apache.openjpa.jdbc.sql.PostgresDictionary");

        Properties properties = databaseConfig.getProperties();

        assertThat(properties.getProperty("javax.persistence.jdbc.driver"), is("org.postgresql.Driver"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), is("jdbc:postgresql:dbserver/stats"));
        assertThat(properties.getProperty("javax.persistence.jdbc.user"), is("admin"));
        assertThat(properties.getProperty("javax.persistence.jdbc.password"), is("password"));
        assertThat(properties.getProperty("openjpa.jdbc.DBDictionary"), is("org.apache.openjpa.jdbc.sql.PostgresDictionary"));
    }

    private void createEmptyDatabaseFor(String emptyDatabaseUrl) throws SQLException {
        DriverManager.getConnection(emptyDatabaseUrl, "SA", "");
    }

    private void createPreexistingDatabaseFor(String preexistingDatabaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(preexistingDatabaseUrl, "SA", "");
        connection.prepareStatement("CREATE MEMORY TABLE PUBLIC.TESTRUN(ID BIGINT NOT NULL PRIMARY KEY,DURATION BIGINT NOT NULL,EXECUTIONDATE TIMESTAMP,RESULT INTEGER,TITLE VARCHAR(255))")
                .executeUpdate();
    }

    private void deletePreexistingDatabaseFor (String preexistingDatabaseUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(preexistingDatabaseUrl, "SA", "");
        connection.prepareStatement("DROP SCHEMA PUBLIC CASCADE")
                .executeUpdate();
        connection.commit();
    }

}

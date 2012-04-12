package net.thucydides.core.statistics.integration.db;

import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenStartingAndStoppingALocalStatisticsDatabase {

    MockEnvironmentVariables environmentVariables;
    LocalHSqldbServerDatabase localDatabase;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @After
    public void shutdownDatabase() {
       localDatabase.stop();
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private void setupMockEnvironment() throws IOException {
        File workDir = temporaryFolder.newFolder();
        environmentVariables.setProperty("thucydides.home", workDir.getAbsolutePath());
        environmentVariables.setProperty("thucydides.database.port","9112");
    }
    
    @Test
    public void should_start_database_if_not_already_started() throws IOException, SQLException {

        setupMockEnvironment();

        environmentVariables.setProperty("thucydides.project.key","integration-test-db1");
        localDatabase = new LocalHSqldbServerDatabase(environmentVariables);
        localDatabase.start();
        
        DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9112/stats-integration-test-db1","SA","");

        assertThat(localDatabase.isAvailable(), is(true));
    }

    @Test
    public void should_shutdown_database_at_the_end_of_the_tests() throws IOException, SQLException {

        setupMockEnvironment();

        environmentVariables.setProperty("thucydides.project.key","integration-test-db2");
        localDatabase = new LocalHSqldbServerDatabase(environmentVariables);
        localDatabase.start();

        DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9112/stats-integration-test-db2", "SA","");

        localDatabase.stop();

        assertThat(localDatabase.isAvailable(), is(false));
    }

    @Test
    public void database_can_be_shutdown_multiple_times() throws IOException, SQLException {

        setupMockEnvironment();

        environmentVariables.setProperty("thucydides.project.key","integration-test-db3");
        localDatabase = new LocalHSqldbServerDatabase(environmentVariables);
        localDatabase.start();

        DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9112/stats-integration-test-db3", "SA","");

        localDatabase.stop();
        localDatabase.stop();
        localDatabase.stop();

        assertThat(localDatabase.isAvailable(), is(false));
    }

}

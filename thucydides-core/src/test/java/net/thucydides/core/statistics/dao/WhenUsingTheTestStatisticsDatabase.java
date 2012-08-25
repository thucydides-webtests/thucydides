package net.thucydides.core.statistics.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.*;
import net.thucydides.core.jpa.JPAProvider;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.LocalPreferences;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.util.PropertiesFileLocalPreferences;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenUsingTheTestStatisticsDatabase {

    Injector injector;
    MockEnvironmentVariables environmentVariables;
    ThucydidesModuleWithMockEnvironmentVariables guiceModule;

    class ThucydidesModuleWithMockEnvironmentVariables extends ThucydidesModule {
        @Override
        protected void configure() {
            clearEntityManagerCache();
            bind(EnvironmentVariables.class).to(MockEnvironmentVariables.class).in(Singleton.class);
            bind(DatabaseConfig.class).to(EnvironmentVariablesDatabaseConfig.class).in(Singleton.class);
            bind(LocalPreferences.class).to(PropertiesFileLocalPreferences.class).in(Singleton.class);
            bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
            bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
            bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
        }
    }

    @Before
    public void setupInjectors() {
        guiceModule = new ThucydidesModuleWithMockEnvironmentVariables();
        injector = Guice.createInjector(guiceModule);
        environmentVariables = (MockEnvironmentVariables) injector.getInstance(EnvironmentVariables.class);
    }

    @Test
    public void should_be_able_to_define_statistics_database_via_hibernate_system_properties() {

        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.Hibernate.name());
        environmentVariables.setProperty("thucydides.statistics.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        environmentVariables.setProperty("thucydides.statistics.url", "jdbc:hsqldb:mem:test");
        environmentVariables.setProperty("thucydides.statistics.username", "admin");
        environmentVariables.setProperty("thucydides.statistics.password", "password");

        JPATestOutcomeHistoryDAO dao = injector.getInstance(JPATestOutcomeHistoryDAO.class);

        Map properties = dao.entityManagerFactory.getProperties();

        assertThat((String)properties.get("hibernate.connection.driver_class"), is("org.hsqldb.jdbc.JDBCDriver"));
        assertThat((String)properties.get("hibernate.connection.url"), is("jdbc:hsqldb:mem:test"));
        assertThat((String)properties.get("hibernate.connection.username"), is("admin"));
        assertThat((String)properties.get("hibernate.connection.password"), is("password"));
        assertThat((String)properties.get("thucydides.jpa.provider"), is(JPAProvider.Hibernate.name()));
    }

    @Test
    public void should_be_able_to_define_statistics_database_via_eclipselink_system_properties() {

        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.EclipseLink.name());
        environmentVariables.setProperty("thucydides.statistics.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        environmentVariables.setProperty("thucydides.statistics.url", "jdbc:hsqldb:mem:test");
        environmentVariables.setProperty("thucydides.statistics.username", "admin");
        environmentVariables.setProperty("thucydides.statistics.password", "password");

        JPATestOutcomeHistoryDAO dao = injector.getInstance(JPATestOutcomeHistoryDAO.class);

        Map properties = dao.entityManagerFactory.getProperties();

        assertThat((String)properties.get("javax.persistence.jdbc.driver"), is("org.hsqldb.jdbc.JDBCDriver"));
        assertThat((String)properties.get("javax.persistence.jdbc.url"), is("jdbc:hsqldb:mem:test"));
        assertThat((String)properties.get("javax.persistence.jdbc.user"), is("admin"));
        assertThat((String)properties.get("javax.persistence.jdbc.password"), is("password"));
        assertThat((String)properties.get("thucydides.jpa.provider"), is(JPAProvider.EclipseLink.name()));

    }

    @Test
    public void should_be_able_to_define_statistics_database_via_openJPA_system_properties() {

        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.OpenJPA.name());
        environmentVariables.setProperty("thucydides.statistics.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        environmentVariables.setProperty("thucydides.statistics.url", "jdbc:hsqldb:mem:test");
        environmentVariables.setProperty("thucydides.statistics.username", "admin");
        environmentVariables.setProperty("thucydides.statistics.password", "password");

        JPATestOutcomeHistoryDAO dao = injector.getInstance(JPATestOutcomeHistoryDAO.class);

        Map properties = dao.entityManagerFactory.getProperties();

        assertThat((String)properties.get("javax.persistence.jdbc.driver"), is("org.hsqldb.jdbc.JDBCDriver"));
        assertThat((String)properties.get("javax.persistence.jdbc.url"), is("jdbc:hsqldb:mem:test"));
        assertThat((String)properties.get("javax.persistence.jdbc.user"), is("admin"));
        assertThat((String)properties.get("VendorName"), is(JPAProvider.OpenJPA.name()));

    }


    @Test
    public void should_obtain_dao_instances_from_the_guice_persistence_module() {
        TestOutcomeHistoryDAO dao = Injectors.getInjector().getInstance(TestOutcomeHistoryDAO.class);

        assertThat(dao, is(notNullValue()));

        assertThat(((JPATestOutcomeHistoryDAO)dao).entityManagerFactory, is(notNullValue()));
    }

    @Test
    public void the_current_working_directory_is_used_as_the_default_project_key() {
        String workingDirPath = System.getProperty("user.dir");
        String workingDirName = new File(workingDirPath).getName();
        assertThat(Thucydides.getDefaultProjectKey(), is(workingDirName));
    }

}

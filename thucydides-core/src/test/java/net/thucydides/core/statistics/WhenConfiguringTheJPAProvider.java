package net.thucydides.core.statistics;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.EnvironmentVariablesDatabaseConfig;
import net.thucydides.core.jpa.JPAProvider;
import net.thucydides.core.jpa.JPAProviderConfig;
import net.thucydides.core.jpa.JPAProviderConfigFactory;
import net.thucydides.core.statistics.dao.JPATestOutcomeHistoryDAO;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.statistics.database.LocalH2ServerDatabase;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: rahul
 * Date: 7/1/12
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhenConfiguringTheJPAProvider {

    EnvironmentVariables environmentVariables;
    LocalDatabase localDatabase;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();
        localDatabase = new LocalH2ServerDatabase(environmentVariables);
    }


    @Test
    public void should_set_the_right_persistence_unit_name_for_hibernate() {
        assertThat(JPAProvider.Hibernate.getPersistenceUnitName(), is("db-manager"));
    }

    @Test
    public void should_set_the_right_persistence_unit_name_for_EclipseLink() {
        assertThat(JPAProvider.EclipseLink.getPersistenceUnitName(), is("db-manager-EclipseLink"));
    }

    @Test
    public void should_configure_EclipseLink_by_default() {

        JPAProviderConfig providerConfig = JPAProviderConfigFactory.getJPAProviderConfig(environmentVariables, localDatabase);
        assertThat(providerConfig.getProvider(), is(JPAProvider.Hibernate));
    }

    @Test
    public void should_be_able_to_configure_Hibernate_from_system_property() {

        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.Hibernate.name());
        JPAProviderConfig providerConfig = JPAProviderConfigFactory.getJPAProviderConfig(environmentVariables, localDatabase);
        assertThat(providerConfig.getProvider(), is(JPAProvider.Hibernate));
    }

    @Test
    public void should_be_able_to_configure_EclipseLink_from_system_property() {

        environmentVariables.setProperty(ThucydidesSystemProperty.JPA_PROVIDER.getPropertyName(), JPAProvider.EclipseLink.name());
        JPAProviderConfig providerConfig = JPAProviderConfigFactory.getJPAProviderConfig(environmentVariables, localDatabase);
        assertThat(providerConfig.getProvider(), is(JPAProvider.EclipseLink));
    }

}

package net.thucydides.core.statistics.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.guice.ThucydidesModule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class WhenUsingTheTestStatisticsDatabase {

    @Test
    public void should_obtain_dao_instances_from_the_guice_persistence_module() {
        TestOutcomeHistoryDAO dao = Injectors.getInjector().getInstance(TestOutcomeHistoryDAO.class);
        assertThat(dao, is(notNullValue()));

        assertThat(dao.entityManager, is(notNullValue()));
    }


}

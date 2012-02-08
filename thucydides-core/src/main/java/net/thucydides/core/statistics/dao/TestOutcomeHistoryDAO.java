package net.thucydides.core.statistics.dao;

import com.google.inject.Inject;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.model.TestRun;

import javax.persistence.EntityManager;
import java.util.List;

public class TestOutcomeHistoryDAO {

    private static final String FIND_ALL_TEST_HISTORIES = "select t from TestRun t order by t.executionDate";
    private static final String FIND_BY_NAME = "select t from TestRun t where t.title = :title";
    private static final String COUNT_BY_NAME = "select count(t) from TestRun t where t.title = :title";
    private static final String COUNT_TESTS_BY_NAME_AND_RESULT
            = "select count(t) from TestRun t where t.title = :title and t.result = :result";
    protected EntityManager entityManager;

    @Inject
    private final SystemClock clock;

    @Inject
    public TestOutcomeHistoryDAO(EntityManager entityManager, SystemClock clock) {
        this.entityManager = entityManager;
        this.clock = clock;
    }

    public List<TestRun> findAll() {
        return entityManager.createQuery(FIND_ALL_TEST_HISTORIES).getResultList();
    }

    public List<TestRun> findTestRunsByTitle(String title) {
        return (List<TestRun>) entityManager.createQuery(FIND_BY_NAME).setParameter("title", title).getResultList();
    }

    public void storeTestOutcome(TestOutcome testResult) {
        TestRun storedHistory = TestRun.from(testResult).at(clock.getCurrentTime().toDate());

        entityManager.getTransaction().begin();
        entityManager.persist(storedHistory);
        entityManager.getTransaction().commit();
    }

    public Long countTestRunsByTitle(String title) {
        return (Long) entityManager.createQuery(COUNT_BY_NAME).setParameter("title", title).getSingleResult();
    }

    public Long countPassingTestRunsByTitle(String title) {
        return (Long) entityManager.createQuery(COUNT_TESTS_BY_NAME_AND_RESULT)
                                   .setParameter("title", title)
                                   .setParameter("result", TestResult.SUCCESS)
                                   .getSingleResult();
    }


}

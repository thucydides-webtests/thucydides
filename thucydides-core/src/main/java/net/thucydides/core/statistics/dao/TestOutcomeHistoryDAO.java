package net.thucydides.core.statistics.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderService;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestOutcomeHistoryDAO {

    private static final String FIND_ALL_TEST_HISTORIES = "select t from TestRun t order by t.executionDate";
    private static final String FIND_BY_NAME = "select t from TestRun t where t.title = :title";
    private static final String FIND_TAG_BY_NAME = "select t from TestRunTag t where t.name = :name and t.code = :code";
    private static final String FIND_ALL_TAGS  = "select t from TestRunTag t order by t.name";
//    private static final String FIND_ALL_TAGS_FOR_CODE  = "select t from TestRunTag t where t.code = :code order by t.name";
    private static final String COUNT_BY_NAME = "select count(t) from TestRun t where t.title = :title";
    private static final String COUNT_TESTS_BY_NAME_AND_RESULT
            = "select count(t) from TestRun t where t.title = :title and t.result = :result";

    private static final String COUNT_BY_TAG = "select count(t) from TestRun t where t.title = :title";

    private static final String SELECT_LATEST_TEST_BY_TITLE
            = "select t from TestRun t \n"+
            "where t.title = :title and t.executionDate = \n"+
            "     (select max(tt.executionDate) from TestRun tt where tt.id = t.id)";

    protected EntityManager entityManager;

    @Inject
    private final SystemClock clock;

    private List<TagProvider> tagProviders;


    @Inject
    public TestOutcomeHistoryDAO(EntityManager entityManager, SystemClock clock) {
        this.entityManager = entityManager;
        this.clock = clock;
        tagProviders = TagProviderService.getTagProviders();
    }

    public List<TestRun> findAll() {
        return entityManager.createQuery(FIND_ALL_TEST_HISTORIES).getResultList();
    }

    public List<TestRun> findTestRunsByTitle(String title) {
        return (List<TestRun>) entityManager.createQuery(FIND_BY_NAME).setParameter("title", title).getResultList();
    }

    public void storeTestOutcome(TestOutcome testResult) {
        entityManager.getTransaction().begin();

        TestRun storedHistory = TestRun.from(testResult).at(clock.getCurrentTime().toDate());
        addTagsTo(testResult, storedHistory);

        entityManager.persist(storedHistory);

        entityManager.getTransaction().commit();
    }

    private void addTagsTo(TestOutcome testResult, TestRun storedTestRun) {
        for(TagProvider tagProvider : tagProviders) {
            Set<TestRunTag> tags = tagProvider.getTagsFor(testResult);
            List<TestRunTag> matchedTags = Lists.newArrayList();
            for(TestRunTag tag : tags) {
                List<TestRunTag> matchingStoredTags = entityManager.createQuery(FIND_TAG_BY_NAME)
                        .setParameter("name", tag.getName())
                        .setParameter("code", tag.getCode())
                        .getResultList();
                if (!matchingStoredTags.isEmpty()) {
                    TestRunTag firstMatchingTag = matchingStoredTags.get(0);
                    storedTestRun.getTags().add(firstMatchingTag);
                    firstMatchingTag.getTestRuns().add(storedTestRun);
                    matchedTags.add(tag);
                }
            }

            tags.removeAll(matchedTags);

            for(TestRunTag tag : tags) {
                entityManager.persist(tag);
                storedTestRun.getTags().add(tag);
            }
        }
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


    public List<TestRunTag> findAllTags() {
        return entityManager.createQuery(FIND_ALL_TAGS).getResultList();
    }

    public List<TestRunTag> getLatestTagsForTestWithTitleByTitle(String title) {
       List<TestRun> latestTestRuns = entityManager.createQuery(SELECT_LATEST_TEST_BY_TITLE).setParameter("title", title).getResultList();
       if (latestTestRuns.isEmpty()) {
           return Collections.emptyList();
       } else {
           return ImmutableList.copyOf(latestTestRuns.get(0).getTags());
       }
    }

    public Long countTestRunsByTag(String tag) {
        return 0L;
    }

    public Long countPassingTestRunsByTag(String tag) {
        return 0L;
    }

    public List<TestRunTag> getLatestTagsForTestWithTag(String tag) {
        return null;
    }
}

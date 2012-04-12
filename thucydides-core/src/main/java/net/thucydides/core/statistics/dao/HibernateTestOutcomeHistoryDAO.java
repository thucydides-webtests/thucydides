package net.thucydides.core.statistics.dao;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.StatisticsListener;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.util.EnvironmentVariables;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

public class HibernateTestOutcomeHistoryDAO implements TestOutcomeHistoryDAO {

    private static final String FIND_ALL_TEST_HISTORIES = "select t from TestRun t where t.projectKey = :projectKey order by t.executionDate";
    private static final String FIND_BY_NAME = "select t from TestRun t where t.title = :title and t.projectKey = :projectKey";
    private static final String FIND_TAG_BY_NAME = "select t from TestRunTag t where t.name = :name and t.type = :type and t.projectKey = :projectKey";
    private static final String FIND_ALL_TAGS  = "select t from TestRunTag t where t.projectKey = :projectKey order by t.name";
    private static final String FIND_ALL_TAG_TYPES = "select distinct t.type from TestRunTag t where t.projectKey = :projectKey order by t.type";
    private static final String COUNT_BY_NAME = "select count(t) from TestRun t where t.title = :title and t.projectKey = :projectKey";
    private static final String COUNT_TESTS_BY_NAME_AND_RESULT
            = "select count(t) from TestRun t where t.title = :title and t.projectKey = :projectKey and t.result = :result";

    private static final String COUNT_LATEST_TESTS_BY_TAG_AND_RESULT
            = "select count(test) from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.name = :name " +
            "and test.result = :result " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String COUNT_LATEST_TESTS_BY_TAG_TYPE_AND_RESULT
            = "select count(test) from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.type = :type " +
            "and test.result = :result " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String SELECT_LATEST_TEST_BY_TITLE
            = "select t from TestRun t "+
            "where t.title = :title " +
            "and t.projectKey = :projectKey " +
            "and t.executionDate = "+
            "     (select max(tt.executionDate) from TestRun tt where tt.id = t.id)";

    private static final String SELECT_LATEST_TEST_BY_TAG
            = "select test from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.name = :name " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String SELECT_LATEST_TEST_BY_TAG_TYPE
            = "select test from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.type = :type " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String SELECT_TEST_RESULTS_BY_TAG
            = "select test.result from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.name = :name " +
            "and test.projectKey = :projectKey " +
            "order by test.executionDate desc";

    private static final String SELECT_TEST_RESULTS_BY_TAG_TYPE
            = "select test.result from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.type = :type " +
            "and test.projectKey = :projectKey " +
            "order by test.executionDate desc";

    private static final String COUNT_LATEST_TEST_BY_TAG
            = "select count(test) from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.name = :name " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String COUNT_LATEST_TEST_BY_TAG_TYPE
            = "select count(test) from TestRun test "+
            " left outer join test.tags as tag " +
            "where tag.type = :type " +
            "and test.projectKey = :projectKey " +
            "and test.executionDate = "+
            "(select max(tt.executionDate) from TestRun tt where tt.id = test.id)";

    private static final String SELECT_TEST_RESULTS_BY_TITLE
            = "select test.result from TestRun test " +
              "where test.title = :title " +
              "and test.projectKey = :projectKey " +
              "order by test.executionDate desc";

    protected EntityManager entityManager;

    private final SystemClock clock;

    private final EnvironmentVariables environmentVariables;
    
    private List<TagProvider> tagProviders;

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTestOutcomeHistoryDAO.class);

    @Inject
    public HibernateTestOutcomeHistoryDAO(EntityManager entityManager, EnvironmentVariables environmentVariables, SystemClock clock) {
        this.entityManager = entityManager;
        this.environmentVariables = environmentVariables;
        this.clock = clock;
        tagProviders = TagProviderService.getTagProviders();
    }

    @Override
    public List<TestRun> findAll() {
        return entityManager.createQuery(FIND_ALL_TEST_HISTORIES)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }

    @Override
    public List<TestRun> findTestRunsByTitle(String title) {
        return (List<TestRun>) entityManager.createQuery(FIND_BY_NAME)
                                            .setParameter("projectKey", getProjectKey())
                                            .setParameter("title", title)
                                            .getResultList();
    }

    @Override
    public void storeTestOutcomes(List<TestOutcome> testOutcomes) {
        entityManager.getTransaction().begin();
        storeEachOutcomeIn(testOutcomes);
        entityManager.getTransaction().commit();
    }

    private void storeEachOutcomeIn(List<TestOutcome> testOutcomes) {
        for(TestOutcome testOutcome : testOutcomes) {
            TestRun storedHistory = TestRun.from(testOutcome)
                                           .inProject(getProjectKey())
                                           .at(clock.getCurrentTime().toDate());
            addTagsFrom(testOutcome).to(storedHistory);
            entityManager.persist(storedHistory);
        }
    }

    private String getProjectKey() {
        return ThucydidesSystemProperty.PROJECT_KEY.from(environmentVariables,
                                                         Thucydides.DEFAULT_PROJECT_KEY);
    }

    private TagAdder addTagsFrom(TestOutcome testResult) {
        return new TagAdder(testResult);
    }

    private class TagAdder {

        private final TestOutcome testOutcome;

        private TagAdder(TestOutcome testOutcome) {
            this.testOutcome = testOutcome;
        }
        
        public void to(TestRun storedTestRun) {
            for(TagProvider tagProvider : tagProviders) {
                List<TestRunTag> tagsToPersist = convert(tagProvider.getTagsFor(testOutcome), toTestRunTags());

                List<TestRunTag> existingTags = findAndUpdateAnyExistingTags(storedTestRun, tagsToPersist);

                tagsToPersist.removeAll(existingTags);

                for(TestRunTag tag : tagsToPersist) {
                    entityManager.persist(tag);
                    storedTestRun.getTags().add(tag);
                }
            }
        }

        private Converter<TestTag, TestRunTag> toTestRunTags() {
            return new Converter<TestTag, TestRunTag>() {

                @Override
                public TestRunTag convert(TestTag from) {
                    return new TestRunTag(getProjectKey(), from.getType(), from.getName());
                }
            }; 
        }

        private List<TestRunTag> findAndUpdateAnyExistingTags(TestRun storedTestRun, List<TestRunTag> tags) {
            List<TestRunTag> matchedTags = Lists.newArrayList();

            for(TestRunTag tag : tags) {
                Optional<TestRunTag> matchingStoredTag = tagInDatabaseMatching(tag);
                if (matchingStoredTag.isPresent()) {
                    TestRunTag matchingTag = matchingStoredTag.get();
                    storedTestRun.getTags().add(matchingTag);
                    matchingTag.getTestRuns().add(storedTestRun);
                    matchedTags.add(tag);
                }
            }
            return ImmutableList.copyOf(matchedTags);
        }

        private Optional<TestRunTag> tagInDatabaseMatching(TestRunTag tag) {
            List<TestRunTag> matchingStoredTags = entityManager.createQuery(FIND_TAG_BY_NAME)
                                    .setParameter("name", tag.getName())
                                    .setParameter("type", tag.getType())
                                    .setParameter("projectKey", tag.getProjectKey())
                                    .getResultList();

            if (matchingStoredTags.isEmpty()) {
                return Optional.absent();
            } else {
                return Optional.of(matchingStoredTags.get(0));
            }
        }
    }

    @Override
    public Long countTestRunsByTitle(String title) {
        return (Long) entityManager.createQuery(COUNT_BY_NAME)
                                   .setParameter("title", title)
                                   .setParameter("projectKey", getProjectKey())
                                   .getSingleResult();
    }

    @Override
    public Long countTestRunsByTitleAndResult(String title, TestResult result) {
        return (Long) entityManager.createQuery(COUNT_TESTS_BY_NAME_AND_RESULT)
                                   .setParameter("title", title)
                                   .setParameter("result", result)
                                   .setParameter("projectKey", getProjectKey())
                                   .getSingleResult();
    }


    @Override
    public List<TestRunTag> findAllTags() {
        return entityManager.createQuery(FIND_ALL_TAGS)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }

    @Override
    public List<TestRunTag> getLatestTagsForTestWithTitleByTitle(String title) {
       List<TestRun> latestTestRuns = entityManager.createQuery(SELECT_LATEST_TEST_BY_TITLE)
                                                   .setParameter("title", title)
                                                   .setParameter("projectKey", getProjectKey())
                                                   .getResultList();
       if (latestTestRuns.isEmpty()) {
           return Collections.emptyList();
       } else {
           return ImmutableList.copyOf(latestTestRuns.get(0).getTags());
       }
    }

    @Override
    public List<TestResult> getResultsTestWithTitle(String title) {
        return entityManager.createQuery(SELECT_TEST_RESULTS_BY_TITLE)
                            .setParameter("title", title)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }

    @Override
    public List<TestResult> getResultsForTestsWithTag(String tag) {
        return entityManager.createQuery(SELECT_TEST_RESULTS_BY_TAG)
                            .setParameter("name", tag)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }

    @Override
    public List<TestResult> getResultsForTestsWithTagType(String tagType) {
        return entityManager.createQuery(SELECT_TEST_RESULTS_BY_TAG_TYPE)
                            .setParameter("type", tagType)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }

    @Override
    public Long countTestRunsByTag(String tag) {
        return (Long) entityManager.createQuery(COUNT_LATEST_TEST_BY_TAG)
                                   .setParameter("name", tag)
                                   .setParameter("projectKey", getProjectKey())
                                   .getSingleResult();
    }

    @Override
    public Long countTestRunsByTagType(String tagType) {
        return (Long) entityManager.createQuery(COUNT_LATEST_TEST_BY_TAG_TYPE)
                .setParameter("type", tagType)
                .setParameter("projectKey", getProjectKey())
                .getSingleResult();
    }

    @Override
    public Long countTestRunsByTagAndResult(String tag, TestResult result) {
        return (Long) entityManager.createQuery(COUNT_LATEST_TESTS_BY_TAG_AND_RESULT)
                .setParameter("name", tag)
                .setParameter("result",result)
                .setParameter("projectKey", getProjectKey())
                .getSingleResult();
    }

    @Override
    public Long countTestRunsByTagTypeAndResult(String tagType, TestResult result) {
        return (Long) entityManager.createQuery(COUNT_LATEST_TESTS_BY_TAG_TYPE_AND_RESULT)
                .setParameter("type", tagType)
                .setParameter("result",result)
                .setParameter("projectKey", getProjectKey())
                .getSingleResult();
    }

    @Override
    public List<TestRunTag> getLatestTagsForTestsWithTag(String tag) {
        List<TestRun> latestTestRuns = entityManager.createQuery(SELECT_LATEST_TEST_BY_TAG)
                                                    .setParameter("name", tag)
                                                    .setParameter("projectKey", getProjectKey())
                                                    .getResultList();
        if (latestTestRuns.isEmpty()) {
            return Collections.emptyList();
        } else {
            return ImmutableList.copyOf(latestTestRuns.get(0).getTags());
        }
    }

    @Override
    public List<TestRunTag> getLatestTagsForTestsWithTagType(String tagType) {
        List<TestRun> latestTestRuns = entityManager.createQuery(SELECT_LATEST_TEST_BY_TAG_TYPE)
                                                    .setParameter("type", tagType)
                                                    .setParameter("projectKey", getProjectKey())
                                                    .getResultList();
        if (latestTestRuns.isEmpty()) {
            return Collections.emptyList();
        } else {
            return ImmutableList.copyOf(latestTestRuns.get(0).getTags());
        }
    }

    @Override
    public List<String> findAllTagTypes() {
        return entityManager.createQuery(FIND_ALL_TAG_TYPES)
                            .setParameter("projectKey", getProjectKey())
                            .getResultList();
    }
}

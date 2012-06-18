package net.thucydides.jbehave.internals;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesListeners;
import net.thucydides.core.ThucydidesReports;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.util.Inflector;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.webdriver.Configuration;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.model.StoryDuration;
import org.jbehave.core.reporters.StoryReporter;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

public class ThucydidesReporter implements StoryReporter {

    private ThucydidesListeners thucydidesListeners;
    private ReportService reportService;
    private final Configuration systemConfiguration;

    public ThucydidesReporter(Configuration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public void storyNotAllowed(Story story, String s) {
    }

    public void storyCancelled(Story story, StoryDuration storyDuration) {
    }

    public void beforeStory(Story story, boolean b) {
        String storyName = removeSuffixFrom(story.getName());
        String storyTitle = NameConverter.humanize(storyName);
        reportService  = ThucydidesReports.getReportService(systemConfiguration);
        thucydidesListeners = ThucydidesReports.setupListeners(systemConfiguration);
        StepEventBus.getEventBus().testSuiteStarted(net.thucydides.core.model.Story.withId(storyName, storyTitle));
        registerStoryIssues(story.getMeta());
        registerStoryFeatures(story.getMeta());
        registerStoryTags(story.getMeta());
    }

    private List<String> getIssueOrIssuesPropertyValues(Meta metaData) {
        return getTagPropertyValues(metaData, "issue");
    }

    private List<TestTag> getFeatureOrFeaturesPropertyValues(Meta metaData) {
        List<String> features = getTagPropertyValues(metaData, "feature");
        return convert(features, toFeatureTags());
    }

    private List<TestTag> getTagOrTagsPropertyValues(Meta metaData) {
        List<String> tags = getTagPropertyValues(metaData, "tag");
        return convert(tags, toTags());
    }

    private Converter<String, TestTag> toTags() {
        return new Converter<String, TestTag>() {
            @Override
            public TestTag convert(String tag) {
                List<String> tagParts = Lists.newArrayList(Splitter.on(":").trimResults().split(tag));
                return TestTag.withName(tagParts.get(1)).andType(tagParts.get(0));
            }
        };
    }

    private Converter<String, TestTag> toFeatureTags() {
        return new Converter<String, TestTag>() {
            @Override
            public TestTag convert(String featureName) {
                return TestTag.withName(featureName).andType("feature");
            }
        };
    }

    private List<String> getTagPropertyValues(Meta metaData, String tagType) {
        String singularTag = metaData.getProperty(tagType);
        String pluralTagType = Inflector.getInstance().pluralize(tagType);

        String multipleTags = metaData.getProperty(pluralTagType);
        String allTags = Joiner.on(',').skipNulls().join(singularTag, multipleTags);

        return Lists.newArrayList(Splitter.on(',').omitEmptyStrings().trimResults().split(allTags));
    }

    private void registerIssues(Meta metaData) {
        List<String> issues = getIssueOrIssuesPropertyValues(metaData);

        if (!issues.isEmpty()) {
            StepEventBus.getEventBus().addIssuesToCurrentTest(issues);
        }
    }

    private void registerStoryIssues(Meta metaData) {
        List<String> issues = getIssueOrIssuesPropertyValues(metaData);

        if (!issues.isEmpty()) {
            StepEventBus.getEventBus().addIssuesToCurrentStory(issues);
        }
    }

    private void registerFeatures(Meta metaData) {
        List<TestTag> features = getFeatureOrFeaturesPropertyValues(metaData);

        if (!features.isEmpty()) {
            StepEventBus.getEventBus().addTagsToCurrentTest(features);
        }
    }

    private void registerStoryFeatures(Meta metaData) {
        List<TestTag> features = getFeatureOrFeaturesPropertyValues(metaData);

        if (!features.isEmpty()) {
            StepEventBus.getEventBus().addTagsToCurrentStory(features);
        }
    }

    private void registerTags(Meta metaData) {
        List<TestTag> tags = getTagOrTagsPropertyValues(metaData);

        if (!tags.isEmpty()) {
            StepEventBus.getEventBus().addTagsToCurrentTest(tags);
        }
    }

    private void registerStoryTags(Meta metaData) {
        List<TestTag> tags = getTagOrTagsPropertyValues(metaData);

        if (!tags.isEmpty()) {
            StepEventBus.getEventBus().addTagsToCurrentStory(tags);
        }
    }
    private String removeSuffixFrom(String name) {
        return (name.contains(".")) ? name.substring(0, name.indexOf(".")) :  name;
    }

    public void afterStory(boolean b) {
        StepEventBus.getEventBus().testSuiteFinished();
        generateReportsFor(thucydidesListeners.getResults());
    }

    private void generateReportsFor(final List<TestOutcome> testRunResults) {
        reportService.generateReportsFor(testRunResults);
    }

    public void narrative(Narrative narrative) {
    }

    public void scenarioNotAllowed(Scenario scenario, String s) {
    }

    public void beforeScenario(String scenarioTitle) {
        StepEventBus.getEventBus().testStarted(scenarioTitle);
    }

    public void scenarioMeta(Meta meta) {
        registerIssues(meta);
        registerFeatures(meta);
        registerTags(meta);
    }

    public void afterScenario() {
        StepEventBus.getEventBus().testFinished();
    }

    public void givenStories(GivenStories givenStories) {
    }

    public void givenStories(List<String> strings) {
    }

    public void beforeExamples(List<String> strings, ExamplesTable examplesTable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void example(Map<String, String> stringStringMap) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterExamples() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void beforeStep(String stepTitle) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(stepTitle));
    }

    public void successful(String title) {
        StepEventBus.getEventBus().updateCurrentStepTitle(title);
        StepEventBus.getEventBus().stepFinished();
    }

    public void ignorable(String title) {
        StepEventBus.getEventBus().updateCurrentStepTitle(title);
        StepEventBus.getEventBus().stepIgnored();
    }

    public void pending(String stepTitle) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(stepTitle));
        StepEventBus.getEventBus().stepPending();
    }

    public void notPerformed(String stepTitle) {
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(stepTitle));
        StepEventBus.getEventBus().stepIgnored();
    }

    public void failed(String stepTitle, Throwable cause) {
        StepEventBus.getEventBus().updateCurrentStepTitle(stepTitle);
        StepEventBus.getEventBus().stepFailed(new StepFailure(ExecutedStepDescription.withTitle(stepTitle), cause));
    }

    public void failedOutcomes(String s, OutcomesTable outcomesTable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void restarted(String s, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dryRun() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void pendingMethods(List<String> strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

package net.thucydides.core.reports.adaptors.specflow;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecflowScenarioTitleLine {
    private final String scenarioTitle;
    private final String storyTitle;
    private final String storyPath;
    private final List<String> parameters = Lists.newArrayList();
    private final Inflector inflector = new Inflector();

    private final EnvironmentVariables environmentVariables;

    public SpecflowScenarioTitleLine(String titleLine, EnvironmentVariables environmentVariables) {
        List<String> titleElements = elementsFrom(stripLead(titleLine));

        this.environmentVariables = environmentVariables;
        scenarioTitle = scenarioTitleIn(Lists.reverse(titleElements).get(0));
        storyTitle = storyTitleIn(titleElements);
        storyPath = pathFrom(titleElements);
    }

    public SpecflowScenarioTitleLine(String titleLine) {
        this(titleLine, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    private String pathFrom(List<String> titleElements) {
        List<String> pathElements = Lists.newArrayList(titleElements);
        pathElements.remove(pathElements.size() - 1);
        pathElements = removeExcludedElementsFrom(pathElements);
        return Joiner.on(".").join(pathElements);
    }

    private List<String> removeExcludedElementsFrom(List<String> pathElements) {
        List<String> purgedPathElements = Lists.newArrayList(pathElements);
        String excludedElementConfiguration
                = environmentVariables.getProperty(ThucydidesSystemProperty.REQUIREMENT_EXCLUSIONS,"");

        List<String> excludedElements
                = Lists.newArrayList(Splitter.on(",")
                                        .trimResults()
                                        .omitEmptyStrings()
                                        .split(excludedElementConfiguration));
        purgedPathElements.removeAll(excludedElements);
        return purgedPathElements;
    }

    private String storyTitleIn(List<String> titleElements) {
        List<String> reverseTitleElements = Lists.reverse(titleElements);
        return inflector.of(reverseTitleElements.get(1)).inHumanReadableForm().toString();
    }

    private String scenarioTitleIn(String titleElement) {
        return Splitter.on("(").split(titleElement).iterator().next();
    }

    private List<String> elementsFrom(String titleElements) {
        return Lists.newCopyOnWriteArrayList(Splitter.on(".").split(titleElements));
    }

    private String stripLead(String titleLine) {
        return StringUtils.strip(titleLine,"* ");
    }

    public String getScenarioTitle() {
        return scenarioTitle;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public String getStoryPath() {
        return storyPath;
    }
}

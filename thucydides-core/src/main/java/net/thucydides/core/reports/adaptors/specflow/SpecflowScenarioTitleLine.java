package net.thucydides.core.reports.adaptors.specflow;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecflowScenarioTitleLine {
    private final String scenarioTitle;
    private final String storyTitle;
    private final List<String> parameters = Lists.newArrayList();

    public SpecflowScenarioTitleLine(String titleLine) {
        List<String> titleElements = elementsFrom(stripLead(titleLine));

        Collections.reverse(titleElements);
        scenarioTitle = scenarioTitleIn(titleElements.get(0));
        storyTitle = storyTitleIn(titleElements);
    }

    private String storyTitleIn(List<String> titleElements) {
        return titleElements.get(1);
    }

    private String scenarioTitleIn(String titleElement) {
        return Splitter.on("(").split(titleElement).iterator().next();
    }

    private List<String> elementsFrom(String titleElements) {
        return Lists.newArrayList(Splitter.on(".").split(titleElements));
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
}

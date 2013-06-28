package net.thucydides.core.reports.adaptors.specflow;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SpecflowScenarioTitleLine {
    private final String scenarioTitle;
    private final String storyTitle;
    private final String storyPath;
    private final List<String> parameters;
    private final Inflector inflector = new Inflector();

    private final EnvironmentVariables environmentVariables;

    public SpecflowScenarioTitleLine(String titleLine, EnvironmentVariables environmentVariables) {
        List<String> titleElements = elementsFrom(stripLead(titleLine));

        this.environmentVariables = environmentVariables;
        scenarioTitle = scenarioTitleIn(Lists.reverse(titleElements).get(0));
        storyTitle = storyTitleIn(titleElements);
        storyPath = pathFrom(titleElements);
        parameters = argumentsFrom(titleLine);
    }

    /**
     * From a title line that looks like:
     * ***** ESD.Epp.RegularPaymentCapture.SpecFlow.Features.RegularPaymentGroupServiceCallsFeature.PopulateBusinessTransactionAndPaymentTypeDropDownLists("Inputter","Funds Transfer between Own Accounts","N/A","Funds Transfer",null)
     * returns a list of string: ["Inputter","Funds Transfer between Own Accounts","N/A","Funds Transfer",""]
     * It assumes that the escape character is '\' and it replaces null by an empty string
     * @param titleLine the title line
     * @return a list of argumenents
     */
    private List<String> argumentsFrom(String titleLine) {
        if(!titleLine.contains("(")) {
            return Lists.newArrayList();
        }
        String argumentString = titleLine.substring(titleLine.indexOf('(') + 1, titleLine.lastIndexOf(')'));
        List<String> result = Lists.newArrayList();
        StringBuilder currentResult = new StringBuilder();
        int state = 0;//0 ok, 1 next is escaped
        boolean inString = false;
        for (int i = 0; i < argumentString.length(); i++) {
            Character c = argumentString.charAt(i);
            if(state == 1) {
                currentResult.append(c);
                state = 0;
            } else if(c == '\\') {
                state = 1;
                currentResult.append(c);
            } else if(c == '"'){//this should happen only while parsing string
                inString = !inString;
            } else if(c == ',' && !inString) {
                String s = currentResult.toString();
                if(s.equals("null")) {
                    s = "";
                }
                result.add(s);
                currentResult = new StringBuilder();
            } else {
                currentResult.append(c);
            }
        }
        if(currentResult.length() != 0) {
            String s = currentResult.toString();
            if(s.equals("null")) {
                s = "";
            }
            result.add(s);
        }
        return result;
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

    public String getTitleName() {
        return storyPath + "." + scenarioTitle;
    }

    public List getArguments() {
        return parameters;
    }
}

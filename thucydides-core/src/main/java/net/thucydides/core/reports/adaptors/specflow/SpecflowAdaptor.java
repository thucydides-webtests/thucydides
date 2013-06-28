package net.thucydides.core.reports.adaptors.specflow;

import ch.lambdaj.function.convert.Converter;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Loads TestOutcomes from a specflow output file
 */
public class SpecflowAdaptor implements TestOutcomeAdaptor {

    private static final String TITLE_LEAD = "***** ";

    /**
     * Loads TestOutcomes from a SpecFlow output file or a directory containing output files.
     *
     * This is the console output, not the XML output file, which does not contain the details about each step
     * execution.
     */
    @Override
    public List<TestOutcome> loadOutcomesFrom(File source) throws IOException {
        if (source.isDirectory()) {
            List<TestOutcome> outcomes = Lists.newArrayList();
            for(File outputFile : source.listFiles()) {
                outcomes.addAll(outcomesFromFile(outputFile));
            }
            return outcomes;
        } else {
            return outcomesFromFile(source);
        }
    }

    private List<TestOutcome> outcomesFromFile(File outputFile) throws IOException {
        List<String> outputLines = FileUtils.readLines(outputFile, Charset.defaultCharset());
        return convert(scenarioOutputsFrom(outputLines), toTestOutcomes());
    }

    private Converter<List<String>, TestOutcome> toTestOutcomes() {
        return new Converter<List<String>, TestOutcome>() {

            @Override
            public TestOutcome convert(List<String> outputLines) {
                SpecflowScenarioTitleLine titleLine = new SpecflowScenarioTitleLine(outputLines.get(0));
                Story story = Story.called(titleLine.getStoryTitle()).withPath(titleLine.getStoryPath());
                TestOutcome outcome = TestOutcome.forTestInStory(titleLine.getScenarioTitle(), story);
                List<List<String>> scenarios = splitScenarios(outputLines);
                List<DataTableRow> rows = Lists.newArrayList();
                for (int i = 0; i < scenarios.size(); i++) {
                    if (i == 0) {
                        List<TestStep> steps = stepsFrom(tail(scenarios.get(0)));
                        outcome.recordSteps(steps);
                    }
                    List<String> scenarioOutput = scenarios.get(i);
                    List<TestStep> steps = stepsFrom(tail(scenarioOutput));
                    titleLine = new SpecflowScenarioTitleLine(scenarioOutput.get(0));
                    DataTableRow dataTableRow = new DataTableRow(titleLine.getArguments());
                    for (int j = 0; j < steps.size(); j++) {
                        TestStep step = steps.get(j);
                        if (!step.getResult().equals(TestResult.SUCCESS) || j == steps.size() - 1) {
                            dataTableRow.setResult(step.getResult());
                            break;
                        }
                    }
                    rows.add(dataTableRow);
                }
                if (!rows.isEmpty()) {
                    DataTable dt = DataTable.withHeaders(Lists.newArrayList("Description", "Error message", "Duration", "Result")).build();
                    dt.addRows(rows);
                    outcome.useExamplesFrom(dt);
                }
                return outcome;
            }
        };
    }

    // assuming all the output lines belongs to the same scenario
    // split the lines for each data set
    // returns a list of of string with the title line
    private List<List<String>> splitScenarios(List<String> outputLines) {
        List<List<String>> scenarios = Lists.newArrayList();
        List<String> current = null;
        for (String line : outputLines) {
            if (isTitle(line)) {
                if (current != null) {
                    scenarios.add(current);
                }
                current = Lists.newArrayList();
                current.add(line);
            } else {
                current.add(line);
            }
        }
        scenarios.add(current);
        return scenarios;
    }

    private List<TestStep> stepsFrom(List<String> scenarioOutput) {
        List<TestStep> discoveredSteps = Lists.newArrayList();
        ScenarioStepReader stepReader = new ScenarioStepReader();
        List<String> lines = new ArrayList(scenarioOutput);
        while (!lines.isEmpty()) {
            discoveredSteps.add(stepReader.consumeNextStepFrom(lines));
        }
        return ImmutableList.copyOf(discoveredSteps);
    }

    private List<String> tail(List<String> outlineLines) {
        return ImmutableList.copyOf(outlineLines.subList(1, outlineLines.size()));
    }

    private List<List<String>> scenarioOutputsFrom(List<String> outputLines) {
        List<List<String>> scenarios = Lists.newArrayList();

        List<String> currentScenario = null;
        SpecflowScenarioTitleLine currentTitle = null;
        for (String line : outputLines) {
            if (isTitle(line)) {
                SpecflowScenarioTitleLine newTitleLine = new SpecflowScenarioTitleLine(line);
                if (currentTitle == null || !newTitleLine.getTitleName().equals(currentTitle.getTitleName())) {
                    currentTitle = new SpecflowScenarioTitleLine(line);
                    currentScenario = Lists.newArrayList();
                    scenarios.add(currentScenario);
                }
            }
            if (currentScenario != null) {
                currentScenario.add(line);
            }
        }
        return ImmutableList.copyOf(scenarios);
    }

    private boolean isTitle(String line) {
        return line.trim().startsWith(TITLE_LEAD);
    }
}

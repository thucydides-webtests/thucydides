package net.thucydides.core.reports.adaptors.specflow;

import ch.lambdaj.function.convert.Converter;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
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
                TestOutcome outcome = TestOutcome.forTestInStory(titleLine.getScenarioTitle(),
                                                                 Story.called(titleLine.getStoryTitle()));

                List<TestStep> steps = stepsFrom(tail(outputLines));
                outcome.recordSteps(steps);
                return outcome;
            }
        };
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

    private List<String>  tail(List<String> outlineLines) {
        return ImmutableList.copyOf(outlineLines.subList(1,outlineLines.size()));
    }

    private List<List<String>> scenarioOutputsFrom(List<String> outputLines) {
        List<List<String>> scenarios = Lists.newArrayList();

        List<String> currentScenario = null;
        for(String line : outputLines) {
            if (isTitle(line)) {
                currentScenario = Lists.newArrayList();
                scenarios.add(currentScenario);
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

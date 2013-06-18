package net.thucydides.core.reports.adaptors.xunit;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor;
import net.thucydides.core.reports.adaptors.xunit.io.XUnitFiles;
import net.thucydides.core.reports.adaptors.xunit.model.TestCase;
import net.thucydides.core.reports.adaptors.xunit.model.TestException;
import net.thucydides.core.reports.adaptors.xunit.model.TestSuite;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

public class DefaultXUnitAdaptor implements TestOutcomeAdaptor {
    private final XUnitLoader loader = new BasicXUnitLoader();

    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException {
        List<TestOutcome> loadedOutcomes = Lists.newArrayList();
        for(File xunitFile : XUnitFiles.in(source)) {
            loadedOutcomes.addAll(testOutcomesIn(xunitFile));
        }
        return ImmutableList.copyOf(loadedOutcomes);
    }

    private List<TestOutcome> testOutcomesIn(File xunitFile) throws IOException {
        List<TestSuite> xunitTestSuites =  loader.loadFrom(xunitFile);
        List<TestOutcome> testOutcomes = Lists.newArrayList();
        for(TestSuite testSuite : xunitTestSuites) {
            testOutcomes.addAll(testOutcomesIn(testSuite));
        }
        return ImmutableList.copyOf(testOutcomes);
    }

    private Collection<? extends TestOutcome> testOutcomesIn(TestSuite testSuite) {
        return convert(testSuite.getTestCases(), toTestOutcomes());
    }

    private Converter<TestCase, TestOutcome> toTestOutcomes() {
        return new Converter<TestCase, TestOutcome>() {

            @Override
            public TestOutcome convert(TestCase from) {
                TestOutcome outcome = TestOutcome.forTestInStory(from.getName(), Story.called(from.getClassname()));
                outcome.setDuration(timeAsLong(from.getTime()));
                if (from.getError().isPresent()) {
                    TestException failure = from.getError().get();
                    outcome.setTestFailureCause(failure.asException());
                }
                if (from.getFailure().isPresent()) {
                    TestException failure = from.getFailure().get();
                    outcome.setTestFailureCause(failure.asAssertionFailure());
                }
                return outcome;
            }
        };
    }

    private long timeAsLong(double time) {
        return (time < 1.0) ? 1 : (long) time;
    }
}

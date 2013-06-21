package net.thucydides.core.model.formatters;

import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomeCounter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;

public class FeatureCoverageFormatter {

    private final RequirementsOutcomes outcomes;
    private final NumericalFormatter formatter;

    public FeatureCoverageFormatter(RequirementsOutcomes outcomes) {

        this.outcomes = outcomes;
        formatter = new NumericalFormatter();
    }

    public FormattedPercentageCoverage percentTests(String testType) {
        return percentTests(TestType.valueOf(testType.toUpperCase()));
    }

    public FormattedPercentageCoverage percentTests(TestType testType) {
        return new FormattedPercentageCoverage(testType);
    }

    public FormattedPercentageCoverage percentTests() {
        return new FormattedPercentageCoverage(TestType.ANY);
    }

    public FormattedPercentageCoverage getPercentTests() {
        return percentTests();
    }

    public FormattedPercentageStepCoverage getPercentSteps() {
        return percentSteps();
    }

    public FormattedPercentageStepCoverage percentSteps() {
        return percentSteps(TestType.ANY);
    }

    public FormattedPercentageStepCoverage percentSteps(String testType) {
        return percentSteps(TestType.valueOf(testType.toUpperCase()));
    }

    public FormattedPercentageStepCoverage percentSteps(TestType testType) {
        return new FormattedPercentageStepCoverage(testType);
    }

    public abstract class FormattedCoverage extends TestOutcomeCounter {

        public FormattedCoverage(TestType testType) {
            super(testType);
        }

        public String withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        protected abstract double percentageDeterminedResult();
        protected abstract double percentageWithResult(TestResult expectedResult);

        public String withResult(TestResult expectedResult) {
            return formatter.percentage(percentageWithResult(expectedResult), 1);
        }

        public String withIndeterminateResult() {
            return formatter.percentage(1 - percentageDeterminedResult(), 1);
        }
    }

    public class FormattedPercentageCoverage extends FormattedCoverage {

        public FormattedPercentageCoverage(TestType testType) {
            super(testType);
        }

        @Override
        protected double percentageDeterminedResult() {
            return outcomes.percentage(testType).withResult(TestResult.ERROR)
                    + outcomes.percentage(testType).withResult(TestResult.FAILURE)
                    + outcomes.percentage(testType).withResult(TestResult.SUCCESS);
        }

        @Override
        protected double percentageWithResult(TestResult expectedResult) {
            return outcomes.percentage(testType).withResult(expectedResult);
        }
    }

    public class FormattedPercentageStepCoverage extends FormattedCoverage {

        public FormattedPercentageStepCoverage(TestType testType) {
            super(testType);
        }

        @Override
        protected double percentageDeterminedResult() {
//            return outcomes.percentageSteps(testType).withResult(TestResult.ERROR)
//                    + outcomes.percentageSteps(testType).withResult(TestResult.FAILURE)
//                    + outcomes.percentageSteps(testType).withResult(TestResult.SUCCESS);
            return 0.0;
        }

        @Override
        protected double percentageWithResult(TestResult expectedResult) {
//            return outcomes.percentageSteps(testType).withResult(expectedResult);
            return 0.0;
        }
    }
}


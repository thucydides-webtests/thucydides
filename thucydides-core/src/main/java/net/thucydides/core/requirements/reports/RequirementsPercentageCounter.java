package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestType;
import net.thucydides.core.reports.TestOutcomes;

public class RequirementsPercentageCounter {

        private final TestType testType;
        private final TestOutcomes testOutcomes;
        private final int estimatedTotalTests;

        public RequirementsPercentageCounter(TestType testType,
                                             TestOutcomes testOutcomes,
                                             int estimatedTotalTests) {
            this.testType = testType;
            this.testOutcomes = testOutcomes;
            this.estimatedTotalTests = estimatedTotalTests;
        }

        public Double withResult(String expectedResult) {
            return withResult(TestResult.valueOf(expectedResult.toUpperCase()));
        }

        public Double withResult(TestResult expectedTestResult) {
            int testCount = testOutcomes.count(testType).withResult(expectedTestResult);
            return ((double) testCount) / ((double) estimatedTotalTests);
        }

        public Double withIndeterminateResult() {
            int passingStepCount = testOutcomes.count(testType).withResult(TestResult.SUCCESS);
            int failingStepCount =  testOutcomes.count(testType).withResult(TestResult.FAILURE);
            int errorStepCount =  testOutcomes.count(testType).withResult(TestResult.ERROR);
            int total = estimatedTotalTests;
            return ((total - passingStepCount - failingStepCount - errorStepCount) / (double) total);
        }

    }

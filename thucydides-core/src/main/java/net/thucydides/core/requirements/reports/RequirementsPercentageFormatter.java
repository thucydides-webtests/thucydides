package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.model.TestResult;

public class RequirementsPercentageFormatter {

        private final RequirementsPercentageCounter counter;
        private final NumericalFormatter formatter;

        public RequirementsPercentageFormatter(RequirementsPercentageCounter counter) {
            this.counter = counter;
            formatter = new NumericalFormatter();
        }

        public String withResult(String expectedResult) {
            double result = counter.withResult(expectedResult);
            return  formatter.percentage(result, 1);
        }

        public String withResult(TestResult expectedResult) {
            double result = counter.withResult(expectedResult);
            return formatter.percentage(result, 1);
        }

        public String withIndeterminateResult() {
            double result = counter.withIndeterminateResult();
            return formatter.percentage(result, 1);
        }

    }

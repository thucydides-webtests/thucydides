package net.thucydides.core.reports;

import com.google.common.collect.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.adaptors.TestOutcomeAdaptor;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestOutcomeAdaptorReporter extends ThucydidesReporter {

    private List<TestOutcomeAdaptor> adaptors = Lists.newArrayList();

    public void generateReportsFrom(File sourceDirectory) throws IOException {
        getOutputDirectory().mkdirs();
        for(TestOutcomeAdaptor adaptor : adaptors) {
            List<TestOutcome> outcomes = adaptor.loadOutcomesFrom(sourceDirectory);
            generateReportsFor(outcomes);
        }
    }

    private void generateReportsFor(List<TestOutcome> outcomes) throws IOException {

        AcceptanceTestReporter xmlTestOutcomeReporter = getXMLReporter();
        AcceptanceTestReporter htmlAcceptanceTestReporter = getHTMLReporter();

        TestOutcomes allOutcomes = TestOutcomes.of(outcomes);
        for(TestOutcome outcome : outcomes) {
            xmlTestOutcomeReporter.generateReportFor(outcome, allOutcomes);
            htmlAcceptanceTestReporter.generateReportFor(outcome, allOutcomes);
        }
    }

    private AcceptanceTestReporter getXMLReporter() {
        XMLTestOutcomeReporter reporter = new XMLTestOutcomeReporter();
        reporter.setOutputDirectory(getOutputDirectory());
        return reporter;
    }

    private AcceptanceTestReporter getHTMLReporter() {
        HtmlAcceptanceTestReporter reporter = new HtmlAcceptanceTestReporter();
        reporter.setOutputDirectory(getOutputDirectory());
        return reporter;
    }

    public void registerAdaptor(TestOutcomeAdaptor adaptor) {
        adaptors.add(adaptor);
    }
}

package net.thucydides.maven.plugins;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.reports.ThucydidesReportData;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;

/**
 * Generates a report for the Maven site
 */
public class ThucydidesHTMLReportGenerator {

    public void generateReport(ThucydidesReportData reportData, Sink sink) {
        sink.head();
        generateTitle(sink);
        sink.head_();

        sink.body();

        generateOverviewSection(sink);

        generateFeatureTable(reportData, sink);

        generateStoriesTable(reportData, sink);

        sink.body_();
        sink.flush();
        sink.close();
    }

    private void generateTitle(Sink sink) {
        sink.title();
        sink.text("Thucydides Report");
        sink.title_();
    }

    private void generateOverviewSection(Sink sink) {
        sink.section1();
        sectionTitle(sink, "Overview");

        sink.sectionTitle2();
        sink.link("thucydides/index.html");
        sink.rawText("Dashboard");
        sink.link_();
        sink.sectionTitle2_();

        sink.section1_();
    }

    private void generateFeatureTable(ThucydidesReportData reportData, Sink sink) {
        sink.section1();
        sectionTitle(sink, "Features");

        sink.table();
        sink.tableRow();
        tableHeader(sink, "Feature");
        tableHeader(sink, "Total stories");
        tableHeader(sink, "Passing stories");
        tableHeader(sink, "Pending stories");
        tableHeader(sink, "Failing stories");
        sink.tableRow_();

        List<FeatureResults> featureResults = reportData.getFeatureResults();
        for(FeatureResults featureResult : featureResults) {
            sink.tableRow();
            tableCellWithLink(sink, featureResult.getFeature().getName(),
                    "thucydides/" + featureResult.getStoryReportName());
            tableCell(sink, featureResult.getTotalStories().toString());
            tableCell(sink, featureResult.getPassingTests().toString());
            tableCell(sink, featureResult.getPendingTests().toString());
            tableCell(sink, featureResult.getFailingTests().toString());
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }

    private void generateStoriesTable(ThucydidesReportData reportData, Sink sink) {
        sink.section1();
        sectionTitle(sink, "Stories");

        sink.table();
        sink.tableRow();
        tableHeader(sink, "Story");
        tableHeader(sink, "Total tests");
        tableHeader(sink, "Passing tests");
        tableHeader(sink, "Pending tests");
        tableHeader(sink, "Failing tests");
        sink.tableRow_();

        List<StoryTestResults> storyResults = reportData.getStoryResults();
        for(StoryTestResults storyResult : storyResults) {
            sink.tableRow();
            tableCellWithLink(sink, storyResult.getTitle(),
                    "thucydides/" + storyResult.getReportName(ReportNamer.ReportType.HTML));
            tableCell(sink, Integer.toString(storyResult.getTotal()));
            tableCell(sink, Integer.toString(storyResult.getSuccessCount()));
            tableCell(sink, Integer.toString(storyResult.getPendingCount()));
            tableCell(sink, Integer.toString(storyResult.getFailureCount()));
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }

    private void tableHeader(final Sink sink, final String heading) {
        sink.tableHeaderCell();
        sink.rawText(heading);
        sink.tableHeaderCell_();
    }

    private void tableCell(final Sink sink, final String heading) {
        sink.tableCell();
        sink.rawText(heading);
        sink.tableCell_();
    }

    private void tableCellWithLink(final Sink sink, final String heading, final String link) {
        sink.tableCell();
        sink.link(link);
        sink.rawText(heading);
        sink.link_();
        sink.tableCell_();
    }

    private void sectionTitle(final Sink sink, final String title) {
        sink.sectionTitle1();
        sink.rawText(title);
        sink.sectionTitle1_();
    }
}

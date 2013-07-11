package net.thucydides.maven.plugins;

import net.thucydides.core.reports.TestOutcomes;
import org.apache.maven.doxia.sink.Sink;

/**
 * Generates a report for the Maven site
 */
public class ThucydidesHTMLReportGenerator {

    public void generateReport(TestOutcomes testOutcomes, Sink sink) {
        sink.head();
        generateTitle(sink);
        sink.head_();

        sink.body();

        generateOverviewSection(sink);

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

//        sink.sectionTitle2();
//        sink.link("thucydides/stories.html");
//        sink.rawText("Stories");
//        sink.link_();
//        sink.sectionTitle2_();

        sink.section1_();
    }
//
//    private void generateTables(TestOutcomes testOutcomes, Sink sink) {
//
//        for (String tagType : testOutcomes.getTagTypes()) {
//            generateTableFor(tagType, testOutcomes.withTagType(tagType), sink);
//        }
//    }
//
//    private void generateTableFor(String label, TestOutcomes testOutcomes, Sink sink) {
//
//        Inflector inflection = Inflector.getInstance();
//        String title = inflection.of(label).asATitle().toString();
//
//        sink.section1();
//        sectionTitle(sink, title);
//
//        for (String outcomeType : testOutcomes.getTagsOfType(title)) {
//            sink.table();
//            sink.tableRow();
//            tableHeader(sink, inflection.of(outcomeType).asATitle().toString());
//            tableHeader(sink, "Total tests");
//            tableHeader(sink, "Passing tests");
//            tableHeader(sink, "Pending tests");
//            tableHeader(sink, "Failing tests");
//            tableHeader(sink, "Coverage");
//            sink.tableRow_();
//
//            for(TestOutcome outcomes : testOutcomes.) {
//                sink.tableRow();
//                tableCellWithLink(sink, featureResult.getFeature().getName(),
//                        "thucydides/" + featureResult.getStoryReportName());
//                tableCell(sink, featureResult.getTotalStories().toString());
//                tableCell(sink, featureResult.getPassingTests().toString());
//                tableCell(sink, featureResult.getPendingTests().toString());
//                tableCell(sink, featureResult.getFailingTests().toString());
//
//                String percentageCoverage = String.format("%.1f%%",featureResult.getCoverage() * 100);
//                tableCell(sink, percentageCoverage);
//
//                sink.tableRow_();
//            }
//            sink.table_();
//        }
//        sink.section1_();
//    }
/*
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
        tableHeader(sink, "Coverage");
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

            String percentageCoverage = String.format("%.1f%%",featureResult.getCoverage() * 100);
            tableCell(sink, percentageCoverage);

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
        tableHeader(sink, "Step Coverage");
        sink.tableRow_();

        List<StoryTestResults> storyResults = reportData.getStoryResults();
        for(StoryTestResults storyResult : storyResults) {
            sink.tableRow();
            tableCellWithLink(sink, storyResult.getTitle(),
                    "thucydides/" + storyResult.getReportName(ReportType.HTML));
            tableCell(sink, Integer.toString(storyResult.getTotal()));
            tableCell(sink, Integer.toString(storyResult.getSuccessCount()));
            tableCell(sink, Integer.toString(storyResult.getPendingCount()));
            tableCell(sink, Integer.toString(storyResult.getFailureCount()));


            String percentageCoverage = String.format("%.1f%%",storyResult.getCoverage() * 100);
            tableCell(sink, percentageCoverage);
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }


*/

//    private void tableHeader(final Sink sink, final String heading) {
//        sink.tableHeaderCell();
//        sink.rawText(heading);
//        sink.tableHeaderCell_();
//    }
//
//    private void tableCell(final Sink sink, final String heading) {
//        sink.tableCell();
//        sink.rawText(heading);
//        sink.tableCell_();
//    }
//
//    private void tableCellWithLink(final Sink sink, final String heading, final String link) {
//        sink.tableCell();
//        sink.link(link);
//        sink.rawText(heading);
//        sink.link_();
//        sink.tableCell_();
//    }

    private void sectionTitle(final Sink sink, final String title) {
        sink.sectionTitle1();
        sink.rawText(title);
        sink.sectionTitle1_();
    }
}

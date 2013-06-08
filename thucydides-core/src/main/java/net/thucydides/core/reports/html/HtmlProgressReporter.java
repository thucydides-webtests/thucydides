package net.thucydides.core.reports.html;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.thucydides.core.geometry.Line;
import net.thucydides.core.geometry.Point;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.NumericalFormatter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.history.ProgressSnapshot;
import net.thucydides.core.reports.history.TestHistory;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.Inflector;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;

public class HtmlProgressReporter extends HtmlReporter {

    private static final String DEFAULT_PROGRESS_REPORT = "freemarker/progress-report.ftl";
    private static final String REPORT_NAME = "progress-report.html";

    private final IssueTracking issueTracking;
    private final TestHistory testHistory;

    public HtmlProgressReporter(final IssueTracking issueTracking, final TestHistory testHistory) {
        this.issueTracking = issueTracking;
        this.testHistory = testHistory;
    }

    public File generateReportFor(final RequirementsOutcomes requirementsOutcomes) throws IOException {
        return generateReportFor(requirementsOutcomes, requirementsOutcomes.getTestOutcomes(), REPORT_NAME);

    }

    public File generateReportFor(final RequirementsOutcomes requirementsOutcomes,
                                  final TestOutcomes testOutcomes,
                                  final String filename) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        Map<String,Object> context = new HashMap<String,Object>();

        context.put("progress", getEstimatedDeliveryDate(testHistory.getProgress()));

        context.put("requirements", requirementsOutcomes);
        context.put("testOutcomes", requirementsOutcomes.getTestOutcomes());
        context.put("allTestOutcomes", testOutcomes);
        context.put("reportName", new ReportNameProvider());
        context.put("timestamp", timestampFrom(testOutcomes));

        addFormattersToContext(context);

        String htmlContents = mergeTemplate(DEFAULT_PROGRESS_REPORT).usingContext(context);
        copyResourcesToOutputDirectory();

        return writeReportToOutputDirectory(filename, htmlContents);
    }

    private List<ProgressSnapshot> getEstimatedDeliveryDate(List<ProgressSnapshot> progress) {
        if (progress.size() > 1) {
            ProgressSnapshot firstSnapshot = progress.get(0);
            ProgressSnapshot latestSnapshot = progress.get(progress.size() - 1);
            Optional<Point> intersection = calculateIntersection(progress);
            DateTime origin = firstSnapshot.getTime();
            if (intersection.isPresent() && isAfterLatestSnapshot(origin, intersection.get(), latestSnapshot)) {
                ProgressSnapshot endOfSeriesEntry =  createEndOfSeriesEntry(latestSnapshot, intersection);
                ProgressSnapshot estimatedDoneEntry =  createNewEstimatedDoneDate(origin, latestSnapshot, intersection);
                List<ProgressSnapshot> progressWithEstimatedDone = Lists.newArrayList(progress);
                progressWithEstimatedDone.add(endOfSeriesEntry);
                progressWithEstimatedDone.add(estimatedDoneEntry);
                return progressWithEstimatedDone;
            }
        }
        return progress;
    }

    private ProgressSnapshot createNewEstimatedDoneDate(DateTime origin, ProgressSnapshot latestSnapshot, Optional<Point> intersection) {
        long dateTimeInstant = origin.getMillis()
                               + intersection.get().getX().round(new MathContext(0, RoundingMode.HALF_UP)).longValue();
        DateTime estimationCompletionDate = new DateTime(dateTimeInstant);
        return ProgressSnapshot.forRequirementType(latestSnapshot.getRequirementType())
                .atTime(estimationCompletionDate)
                .with(latestSnapshot.getTotal()).estimated()
                .and(0).failed()
                .and(0).completed()
                .outOf(latestSnapshot.getTotal())
                .forBuild("ESTIMATED_DONE_DATE");
    }

    private ProgressSnapshot createEndOfSeriesEntry(ProgressSnapshot latestSnapshot, Optional<Point> intersection) {
        return ProgressSnapshot.forRequirementType(latestSnapshot.getRequirementType())
                .atTime(latestSnapshot.getTime().plus(1).secondOfDay().getDateTime())
                .and(0).failed()
                .and(0).completed()
                .and(latestSnapshot.getCompleted()).estimated()
                .outOf(latestSnapshot.getTotal())
                .forBuild("ESTIMATED_DONE_DATE");
    }


    private Optional<Point> calculateIntersection(List<ProgressSnapshot> progressSnapshots) {
        ProgressSnapshot firstSnapshot = progressSnapshots.get(0);
        ProgressSnapshot firstSnapshotWithSpecifications = firstSnapshotWithSpecificationsIn(progressSnapshots);
        ProgressSnapshot latestSnapshot = progressSnapshots.get(progressSnapshots.size() - 1);

        Line doneLine = calculateDoneLine(firstSnapshot, latestSnapshot);
        Line specifiedLine = calculateSpecifiedLine(firstSnapshotWithSpecifications, latestSnapshot);
        return doneLine.intersectionWith(specifiedLine);
    }

    private ProgressSnapshot firstSnapshotWithSpecificationsIn(List<ProgressSnapshot> progressSnapshots) {
        for(ProgressSnapshot progressSnapshot : progressSnapshots) {
            if (progressSnapshot.getTotal() > 0) {
                return progressSnapshot;
            }
        }
        return progressSnapshots.get(0);
    }

    private boolean isAfterLatestSnapshot(DateTime origin, Point intersection, ProgressSnapshot latestSnapshot) {
        Point finalDonePoint = Point.at(latestSnapshot.getTime().getMillis() - origin.getMillis(),
                                        (long) latestSnapshot.getCompleted());
        return (intersection.getX().compareTo(finalDonePoint.getX()) > 0);
    }

    private Line calculateDoneLine(ProgressSnapshot firstSnapshot, ProgressSnapshot latestSnapshot) {
        DateTime origin = firstSnapshot.getTime();
        Point initialDonePoint = Point.at(normalizedTime(firstSnapshot, origin), (long) firstSnapshot.getCompleted());
        Point finalDonePoint = Point.at(normalizedTime(latestSnapshot, origin), (long) latestSnapshot.getCompleted());
        return Line.from(initialDonePoint).horizontally().to(finalDonePoint);
    }

    private Line calculateSpecifiedLine(ProgressSnapshot firstSnapshot, ProgressSnapshot latestSnapshot) {
        DateTime origin = firstSnapshot.getTime();
        Point initialPoint = Point.at(normalizedTime(firstSnapshot, origin), (long) firstSnapshot.getTotal());
        Point finalPoint = Point.at(normalizedTime(latestSnapshot, origin), (long) latestSnapshot.getTotal());
        return Line.from(initialPoint).horizontally().to(finalPoint);
    }

    private long normalizedTime(ProgressSnapshot snapshot, DateTime origin) {
        return snapshot.getTime().getMillis() - origin.getMillis();
    }

    private void addFormattersToContext(final Map<String, Object> context) {
        Formatter formatter = new Formatter(issueTracking);
        context.put("formatter", formatter);

        context.put("formatted", new NumericalFormatter());
        context.put("inflection", Inflector.getInstance());
    }
}

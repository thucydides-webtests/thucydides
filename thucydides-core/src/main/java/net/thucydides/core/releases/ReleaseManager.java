package net.thucydides.core.releases;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static org.hamcrest.Matchers.containsString;

public class ReleaseManager {

    private final String DEFAULT_RELEASE_TYPES = "Release,Iteration";
    private List<String> releaseTypes;
    private ReportNameProvider reportNameProvider;

    public ReleaseManager(EnvironmentVariables environmentVariables, ReportNameProvider reportNameProvider) {
        this.reportNameProvider = reportNameProvider;
        String typeValues = ThucydidesSystemProperty.RELEASE_TYPES.from(environmentVariables, DEFAULT_RELEASE_TYPES);
        releaseTypes = Splitter.on(",").trimResults().splitToList(typeValues);
    }

    public String getJSONReleasesFrom(TestOutcomes testOutcomes) {
        List<Release> releases = getReleasesFrom(testOutcomes);
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(releases);

    }

    public List<Release> getReleasesFrom(TestOutcomes testOutcomes) {
        List<TestTag> releaseTags = testOutcomes.findMatchingTags()
                                                .withType("version")
                                                .withName(containsString(releaseTypes.get(0)))
                                                .list();
        List<Release> releases = Lists.newArrayList();
        for (TestTag tag : releaseTags) {
            releases.add(extractReleaseFor(tag, testOutcomes.withTag(tag), 1));
        }
        return ImmutableList.copyOf(releases);
    }

    private Release extractReleaseFor(TestTag releaseTag, TestOutcomes testOutcomes, int level) {
        Release release = new Release(releaseTag);
        if (level < releaseTypes.size()) {
            String childReleaseType = releaseTypes.get(level);
            List<TestTag> childReleaseTags = testOutcomes.findMatchingTags()
                                                  .withType("version")
                                                  .withName(containsString(childReleaseType))
                                                  .list();
            List<Release> children = Lists.newArrayList();
            for (TestTag tag : childReleaseTags) {
                children.add(extractReleaseFor(tag, testOutcomes.withTag(tag), level + 1));
            }
            release = release.withChildren(children);
        }
        String reportName = reportNameProvider.forRelease(release);
        return release.withReport(reportName);
    }

}

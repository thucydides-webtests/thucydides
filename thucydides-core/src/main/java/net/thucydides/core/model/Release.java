package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A release or version of a software project.
 * Releases are identified by a tag, usually of type 'version'
 */
public class Release {
    private final TestTag releaseTag;
    private final List<Release> children;
    private final String label;
    private final String reportName;

    public Release(TestTag releaseTag) {
        this.releaseTag = releaseTag;
        this.label = releaseTag.getName();
        this.children = ImmutableList.of();
        this.reportName = null;
    }

    public Release(TestTag releaseTag, List<Release> children, String reportName) {
        this.releaseTag = releaseTag;
        this.label = releaseTag.getName();
        this.children = ImmutableList.copyOf(children);
        this.reportName = reportName;
    }

    public Release withChildren(List<Release> children) {
        return new Release(releaseTag, children, reportName);
    }

    public Release withReport(String reportName) {
        return new Release(releaseTag, children, reportName);
    }
    public String getName() {
        return releaseTag.getName();
    }

    public String getLabel() {
        return label;
    }

    public List<Release> getChildren() {
        return children;
    }

    public TestTag getReleaseTag() {
        return releaseTag;
    }

    public String getReportName() {
        return reportName;
    }
}

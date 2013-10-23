package net.thucydides.core.reports.html;

import com.google.common.base.Optional;
import net.thucydides.core.model.Release;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.NameConverter;

public class ReportNameProvider {

    private final Optional<String> context;
    private final ReportNamer reportNamer;

    private final static Optional<String> NO_CONTEXT = Optional.absent();

    public ReportNameProvider() {
        this(NO_CONTEXT, ReportType.HTML);
    }

    public ReportNameProvider(String context) {
       this(Optional.fromNullable(context), ReportType.HTML);
    }

    protected ReportNameProvider(Optional<String> context, ReportType type) {
        this.context = context;
        this.reportNamer = ReportNamer.forReportType(type);
    }

    public String getContext() {
        if(context.isPresent()) {
            return context.get();
        } else {
            return "";
        }
     }

    public ReportNameProvider forCSVFiles() {
        return new ReportNameProvider(this.context, ReportType.CSV);
    }

    public String forTestResult(String result) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "result_" + result);
    }

    public String forTag(String tag) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "tag_" + tag);
    }

    public String forTagType(String tagType) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "tagtype_" + tagType);
    }

    public String forRequirementType(String tagType) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "requirement_type_" + tagType);
    }

    public ReportNameProvider withPrefix(String prefix) {
        return new ReportNameProvider(prefix);
    }

    private String prefixUsing(Optional<String> context) {
        if (context.isPresent()) {
            return "context_" + NameConverter.underscore(context.get()) + "_";
        } else {
            return "";
        }
    }

    public String forRequirement(Requirement parentRequirement) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "requirement_" + parentRequirement.getName());
    }

    public String forRelease(Release release) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "release_" + release.getName());
    }

    public String forRelease(String releaseName) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "release_" + releaseName);
    }

}

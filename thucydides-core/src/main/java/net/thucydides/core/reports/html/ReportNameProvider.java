package net.thucydides.core.reports.html;

import com.google.common.base.Optional;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.NameConverter;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ReportNameProvider {

    private final Optional<String> context;
    private final ReportNamer reportNamer = ReportNamer.forReportType(ReportType.HTML);

    public ReportNameProvider() {
        this.context = Optional.absent();
    }
    public ReportNameProvider(String context) {
       if (isEmpty(context))
           this.context =  Optional.absent();
        else {
           this.context = Optional.of(context);
       }
    }

    public String forTestResult(String result) {
        return prefixUsing(context) + "result_" + reportNamer.getNormalizedTestNameFor(result);
    }

    public String forTag(String tag) {
        return prefixUsing(context) + "tag_" + reportNamer.getNormalizedTestNameFor(tag);
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

    public String forTagType(String tagType) {
        return "tagtype_" + reportNamer.getNormalizedTestNameFor(tagType);
    }

    public ReportNameProvider inContext(String context) {
        return new ReportNameProvider(context);
    }
}

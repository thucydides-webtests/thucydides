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
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "result_" + result);
    }

    public String forTag(String tag) {
        return reportNamer.getNormalizedTestNameFor(prefixUsing(context) + "tag_" + tag);
    }

    public String forTagType(String tagType) {
        return reportNamer.getNormalizedTestNameFor("tagtype_" + tagType);
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

    public ReportNameProvider inContext(String context) {
        return new ReportNameProvider(context);
    }
}

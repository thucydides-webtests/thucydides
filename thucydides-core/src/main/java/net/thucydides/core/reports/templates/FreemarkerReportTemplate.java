package net.thucydides.core.reports.templates;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerReportTemplate implements ReportTemplate {

    private final Template template;


    public FreemarkerReportTemplate(final Configuration configuration, final String templateFile) throws IOException, TemplateMergeException {
        try {
            template = configuration.getTemplate(templateFile);
        } catch (ParseException parseException) {
            throw new TemplateMergeException("Parsing error in template", parseException);
        }
    }

    public void merge(Map<String, Object> context, StringWriter writer) throws TemplateMergeException {

        try {
            template.process(context, writer);
        } catch (Exception e) {
            throw new TemplateMergeException("Failed to process FreeMarker template", e);
        }
    }
}

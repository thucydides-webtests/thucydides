package net.thucydides.core.reports.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerReportTemplate implements ReportTemplate {

    private final Template template;


    public FreemarkerReportTemplate(final Configuration configuration, final String templateFile) throws IOException {
        template = configuration.getTemplate(templateFile);
    }

    public void merge(Map<String, Object> context, StringWriter writer) throws IOException {

        try {
            template.process(context, writer);
        } catch (TemplateException e) {
            throw new IOException("Failed to process FreeMarker template", e);
        }
    }
}

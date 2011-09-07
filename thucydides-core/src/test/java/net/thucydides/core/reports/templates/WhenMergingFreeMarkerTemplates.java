package net.thucydides.core.reports.templates;


import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenMergingFreeMarkerTemplates {

    @Test
    public void should_load_freemarker_template_from_classpath() throws Exception {
        FreeMarkerTemplateManager templateManager = new FreeMarkerTemplateManager();
        ReportTemplate template = templateManager.getTemplateFrom("templates/test.ftl");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("name","Joe");
        StringWriter sw = new StringWriter();
        template.merge(context, sw);

        assertThat(sw.toString(), is("Hi Joe"));

    }
}

package net.thucydides.core.reports.html;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * Manages velocity templates.
 *
 */
public class TemplateManager {

    private VelocityEngine ve = new VelocityEngine();

    public TemplateManager() {
        ve.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        ve.addProperty("classpath." + Velocity.RESOURCE_LOADER + ".class",
                       "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        ve.init();
    }

    public Template getTemplateFrom(final String path) {
        return ve.getTemplate(path);
    }

}

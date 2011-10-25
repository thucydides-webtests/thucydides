package net.thucydides.core.issues;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.thucydides.core.ThucydidesSystemProperties;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

/**
 * Determine the issue tracking URL formats for a project.
 */
public interface IssueTracking {

    public String getIssueTrackerUrl();

}

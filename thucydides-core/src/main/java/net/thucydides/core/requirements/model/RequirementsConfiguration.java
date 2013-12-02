package net.thucydides.core.requirements.model;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

public class RequirementsConfiguration {
    public final static List<String> DEFAULT_CAPABILITY_TYPES = ImmutableList.of("capability", "feature");
    protected static final String DEFAULT_ROOT_DIRECTORY = "stories";

    private EnvironmentVariables environmentVariables;

    public RequirementsConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> getRequirementTypes() {
        String requirementTypes = ThucydidesSystemProperty.REQUIREMENT_TYPES.from(environmentVariables);
        if (StringUtils.isNotEmpty(requirementTypes)) {
            Iterator<String> types = Splitter.on(",").trimResults().split(requirementTypes).iterator();
            return Lists.newArrayList(types);
        } else {
            return DEFAULT_CAPABILITY_TYPES;
        }
    }

    public String getDefaultRootDirectory() {
        if (ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.from(environmentVariables);
        }
        return DEFAULT_ROOT_DIRECTORY;
    }
}

package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

public class AbstractRequirementsTagProvider {
    public final static List<String> DEFAULT_CAPABILITY_TYPES = ImmutableList.of("capability", "feature");
    protected static final String DEFAULT_ROOT_DIRECTORY = "stories";

    protected final EnvironmentVariables environmentVariables;
    protected final String rootDirectory;

    protected AbstractRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.rootDirectory = getDefaultRootDirectory();
    }

    protected String humanReadableVersionOf(String name) {
        String underscoredName = Inflector.getInstance().underscore(name);
        return Inflector.getInstance().humanize(underscoredName);
    }

    protected String getDefaultType(int level) {
        List<String> types = getRequirementTypes();
        if (level > types.size() - 1) {
            return types.get(types.size() - 1);
        } else {
            return types.get(level);
        }
    }

    protected List<String> getRequirementTypes() {
        String requirementTypes = ThucydidesSystemProperty.REQUIREMENT_TYPES.from(environmentVariables);
        if (StringUtils.isNotEmpty(requirementTypes)) {
            Iterator<String> types = Splitter.on(",").trimResults().split(requirementTypes).iterator();
            return Lists.newArrayList(types);
        } else {
            return DEFAULT_CAPABILITY_TYPES;
        }
    }

    protected String getDefaultRootDirectory() {
        if (ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.ANNOTATED_REQUIREMENTS_DIRECTORY.from(environmentVariables);
        }
        return DEFAULT_ROOT_DIRECTORY;
    }
}

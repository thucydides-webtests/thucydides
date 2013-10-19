package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

public class AbstractRequirementsTagProvider {

    protected final EnvironmentVariables environmentVariables;
    protected final String rootDirectory;
    protected final RequirementsConfiguration requirementsConfiguration;

    protected AbstractRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
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
        return requirementsConfiguration.getRequirementTypes();
    }

    protected String getDefaultRootDirectory() {
        return requirementsConfiguration.getDefaultRootDirectory();
    }
}

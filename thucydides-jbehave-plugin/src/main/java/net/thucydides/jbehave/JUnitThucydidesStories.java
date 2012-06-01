package net.thucydides.jbehave;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.jbehave.internals.ThucydidesJBehave;
import net.thucydides.jbehave.internals.ThucydidesStepFactory;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.steps.InjectableStepsFactory;

import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

/**
 * A JUnit-runnable test case designed to run a set of Thucydides-enabled JBehave stories in a given package.
 * By default, it will look for *.story files on the classpath, and steps in or underneath the current package.
 * You can redefine these constraints as follows:
 */
public abstract class JUnitThucydidesStories extends JUnitStories {

    public static final String DEFAULT_STORY_NAME =  "**/*.story";

    private net.thucydides.core.webdriver.Configuration systemConfiguration;

    private String storyFolder = "";
    private String storyNamePattern = DEFAULT_STORY_NAME;

    @Override
    public Configuration configuration() {
        configure();
        return ThucydidesJBehave.defaultConfiguration(getSystemConfiguration());
    }

    /**
     * Override this method to customize the stories to be executed.
     */
    public void configure() {}

    @Override
    public InjectableStepsFactory stepsFactory() {
        return ThucydidesStepFactory.withStoriesFromPackage(getRootPackage());
    }

    @Override
    protected List<String> storyPaths() {
        System.out.println("Story path: " + getStoryPath());
        System.out.println("Path root: " + codeLocationFromClass(this.getClass()));
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), getStoryPath(), "");
    }

    /**
     * The root package on the classpath containing the JBehave stories to be run.
     */
    protected String getRootPackage() {
        return this.getClass().getPackage().getName();
    }

    protected String getStoryFolder()  {
        return storyFolder;
    }

    protected String getStoryNamePattern() {
        return storyNamePattern;
    }
    /**
     * The root package on the classpath containing the JBehave stories to be run.
     */
    protected String getStoryPath() {
        return (StringUtils.isEmpty(storyFolder)) ? storyNamePattern : storyFolder + "/" + storyNamePattern;
    }

    /**
     * Define the folder on the class path where the stories should be found
     * @param storyFolder
     */
    public void findStoriesIn(String storyFolder) {
        this.storyFolder = storyFolder;
    }


    public void findStoriesCalled(String storyName) {
        if (storyName.startsWith("**/")) {
            storyNamePattern = storyName;
        } else {
            storyNamePattern = "**/" + storyName;
        }

    }

    /**
     * Use this to override the default Thucydides configuration - for testing purposes only.
     */
    public void setSystemConfiguration(net.thucydides.core.webdriver.Configuration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public net.thucydides.core.webdriver.Configuration getSystemConfiguration() {
        if (systemConfiguration == null) {
            systemConfiguration = Injectors.getInjector().getInstance(net.thucydides.core.webdriver.Configuration.class);
        }
        return systemConfiguration;
    }
}

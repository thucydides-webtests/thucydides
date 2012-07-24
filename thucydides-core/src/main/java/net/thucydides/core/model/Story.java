package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.EqualsUtils;
import net.thucydides.core.util.NameConverter;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.joinFrom;
import static net.thucydides.core.model.ReportType.ROOT;

/**
 * Represents a given user story.
 * Used to record test results and so on.
 */
public class Story {

    private final Class<?> userStoryClass;
    private final String qualifiedStoryClassName;
    private final String storyName;
    private final String path;
    private final String qualifiedFeatureClassName;
    private final String featureName;

    protected Story(final Class<?> userStoryClass) {
        this.userStoryClass = userStoryClass;
        this.qualifiedStoryClassName = userStoryClass.getCanonicalName();
        this.storyName = NameConverter.humanize(getUserStoryClass().getSimpleName());
        this.qualifiedFeatureClassName = findFeatureClassName();
        this.featureName = findFeatureName();
        this.path = userStoryClass.getPackage().getName();
    }

    private String findFeatureClassName() {
        if (getFeatureClass() != null) {
            return getFeatureClass().getCanonicalName();
        }
        return null;
    }

    private String findFeatureName() {
        if (getFeatureClass() != null) {
            return ApplicationFeature.from(getFeatureClass()).getName();
        }
        return null;
    }

    protected Story(final String qualifiedStoryClassName, final String storyName,
                    final String qualifiedFeatureClassName, final String featureName,
                    final String path) {
        this.userStoryClass = null;
        this.qualifiedStoryClassName = qualifiedStoryClassName;
        this.storyName = storyName;
        this.qualifiedFeatureClassName = qualifiedFeatureClassName;
        this.featureName = featureName;
        this.path = path;
    }

    public String getId() {
        return qualifiedStoryClassName;
    }
    /**
     * Obtain a story instance from a given story class.
     * Story instances are used for recording and reporting test results.
     */
    public static Story from(final Class<?> userStoryClass) {
        return new Story(userStoryClass);
    }

    /**
     * Create a story using a full class name as an id.
     * Note that the class may no longer exist, so the story needs to be able to exist beyond the life
     * of the original story class. This is used to deserialize stories from XML files.
     */
    public static Story withId(final String storyId, final String storyName) {
        return new Story(storyId, storyName, null, null, null);
    }

    public static Story withIdAndPath(final String storyId, final String storyName, final String storyPath) {
        return new Story(storyId, storyName, null, null, storyPath);
    }

    public static Story called(final String storyName) {
        return new Story(storyName, storyName, null, null, null);
    }

    public static Story withId(final String storyId, final String storyName,
                               final String featureClassName, final String featureName) {
        return new Story(storyId, storyName, featureClassName, featureName, null);
    }


    @Override
    public int hashCode() {
        return nullSafeHashCodeOf(qualifiedStoryClassName);
    }

    private int nullSafeHashCodeOf(final String value) {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Story that = (Story) obj;

        return EqualsUtils.areEqual(this.qualifiedStoryClassName, that.qualifiedStoryClassName);
    }

    /**
     * The underlying user story class that represents this story.
     * This is used to record the original story class in the reports and XML results files.
     */
    public Class<?> getUserStoryClass() {
        return userStoryClass;
    }
    /**
     * What feature does this story belong to?
     */
    public Class<?> getFeatureClass() {
        if (userStoryClass != null) {
            Class<?> enclosingClass = userStoryClass.getEnclosingClass();
            if (isAFeature(enclosingClass)) {
                return enclosingClass;
            }
        }
        return null;
    }

    private boolean isAFeature(Class<?> enclosingClass) {
        return (enclosingClass != null) && (enclosingClass.getAnnotation(Feature.class) != null);
    }

    /**
     * Returns the class representing the story that is tested by a given test class
     * This is indicated by the Story annotation.
     * @return
     */
    public static Class<?> testedInTestCase(Class<?> testClass) {
        net.thucydides.core.annotations.Story story = testClass.getAnnotation(net.thucydides.core.annotations.Story.class);
        if (story != null) {
            return story.value();
        } else {
            return null;
        }
    }

    /**
     * Each story has a descriptive name.
     * This name is usually a human-readable version of the class name, or the story name for an easyb story.
     */
    public String getName() {
        return storyName;
    }


    public String getFeatureName() {
        return featureName;
    }

    /**
     * Find the name of the report for this story for the specified report type (XML, HTML,...).
     */
    public String getReportName(final ReportType type) {
        return Stories.reportFor(this, type);
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    public String getFeatureId() {
        return qualifiedFeatureClassName;
    }

    public ApplicationFeature getFeature() {
        if (getFeatureClass() != null) {
            return ApplicationFeature.from(getFeatureClass());
        } else if (getFeatureId() != null) {
            return new ApplicationFeature(getFeatureId(), getFeatureName());
        } else {
            return null;
        }
    }

    public String getPath() {
        return path;
    }
}

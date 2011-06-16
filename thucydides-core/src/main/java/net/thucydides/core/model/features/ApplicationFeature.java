package net.thucydides.core.model.features;

import net.thucydides.core.util.NameConverter;

/**
 * A feature represents a higher-level functionality that is illustrated by several user stories.
 * This class is used to represent a feature in the test outcomes and reports.
 * This class refers to an underlying class, the featureClass, which refers to the class used by the
 * API user to define the feature and the nested user stories, e.g.
 * <pre>
 *     <code>
 *         @Feature
 *         public class MyFeature {
 *             public class MyUserStory1() {}
 *             public class MyUserStory2() {}
 *         }
 *     </code>
 * </pre>
 *
 * These classes are then used in the test cases and easyb stories to refer to the tested user stories, e.g.
 * <pre>
 *     <code>
 *         @Test
 *         @TestsStory(MyUserStory1.class)
 *         public void should_do_this() {...}
 *     </code>
 * </pre>
 */
public class ApplicationFeature {


    private final Class<?> featureClass;
    private final String featureId;
    private final String featureName;

    public ApplicationFeature(final String featureId, final String featureName) {
        this.featureClass = null;
        this.featureId = featureId;
        this.featureName = featureName;


    }

    protected ApplicationFeature(final Class<?> featureClass) {
        this.featureClass = featureClass;
        this.featureId = null;
        this.featureName = null;
    }

    public String getName() {
        if (featureName == null) {
            return getFeatureName();
        } else {
            return featureName;
        }
    }

    /**
     * The underlying feature class that represents this feature.
     * This is used to record the original feature class in the reports and XML results files.
     */
    public Class<?> getFeatureClass() {
        return featureClass;
    }

    /**
     * Obtain an application feature instance from a given feature class.
     * Feature instances are used for recording and reporting test results.
     */
    public static ApplicationFeature from(final Class<?> featureClass) {
        return new ApplicationFeature(featureClass);
    }

    /**
     * Each feature has a descriptive name.
     * This name is usually a human-readable version of the class name, or the name provided in the ApplicationFeature annotation.
     */
    protected String getFeatureName() {
        return NameConverter.humanize(simpleClassName());
    }

    public String getId() {
        if (featureId == null) {
            return canonicalClassName();
        } else {
            return featureId;
        }
    }

    private String simpleClassName() {
        if (getFeatureClass() != null) {
            return getFeatureClass().getSimpleName();
        } else {
            return "";
        }
    }

    private String canonicalClassName() {
        if (getFeatureClass() != null) {
            return getFeatureClass().getCanonicalName();
        } else {
            return "";
        }
    }

    public boolean classesAreEqual(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationFeature)) return false;

        ApplicationFeature that = (ApplicationFeature) o;

        if (featureClass != null ? !featureClass.equals(that.featureClass) : that.featureClass != null) return false;

        return true;
    }

    public boolean idAndNameAreEqual(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationFeature)) return false;

        ApplicationFeature that = (ApplicationFeature) o;

        if (featureId != null ? !featureId.equals(that.featureId) : that.featureId != null) return false;
        if (featureName != null ? !featureName.equals(that.featureName) : that.featureName != null) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationFeature)) return false;

        ApplicationFeature that = (ApplicationFeature) o;

        if (this.featureClass != null) {
            return classesAreEqual(that);
        } else {
            return idAndNameAreEqual(that);
        }
    }

    @Override
    public int hashCode() {
        int result = featureClass != null ? featureClass.hashCode() : 0;
        result = 31 * result + (featureId != null ? featureId.hashCode() : 0);
        result = 31 * result + (featureName != null ? featureName.hashCode() : 0);
        return result;
    }
}

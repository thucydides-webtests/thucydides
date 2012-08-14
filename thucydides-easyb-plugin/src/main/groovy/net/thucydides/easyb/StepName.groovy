package net.thucydides.easyb

import net.thucydides.core.steps.ScenarioSteps
import net.thucydides.core.util.NameConverter

/**
 * Determine the name by which a step library will be used in an easyb story.
 * By convention, a step library name is converted to lower-case and underscores
 * (e.g. "MyUserStory" becomes "my_user_story"). If the classname ends with
 * 'Steps', this is removed (e.g. "MyUserStorySteps" becomes "my_user_story").
 */
final class StepName {
    private static final String STEPS_SUFFIX = "Steps"
    private static final int STEPS_SUFFIX_LENGTH = STEPS_SUFFIX.size()

    public static String defaultNameOf(final Class<ScenarioSteps> stepLibrary) {
        def normalizedName = stripStepSuffix(stepLibrary.getSimpleName());
        def humanizedName = NameConverter.humanize(normalizedName).toLowerCase()
        return NameConverter.underscore(humanizedName)
    }

    private static String stripStepSuffix(final String classname) {
        if (classname.endsWith(STEPS_SUFFIX)) {
            return classname.substring(0, startOfSuffix(classname))
        } else {
            return classname
        }
    }

    private static int startOfSuffix(final String classname) {
        classname.length() - STEPS_SUFFIX_LENGTH
    }
}

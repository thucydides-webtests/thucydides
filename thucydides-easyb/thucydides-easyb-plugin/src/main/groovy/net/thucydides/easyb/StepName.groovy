package net.thucydides.easyb

import net.thucydides.core.model.ScenarioSteps
import net.thucydides.core.util.NameConverter

class StepName {
    public static String nameOf(Class<ScenarioSteps> stepLibrary) {
        def normalizedName = stripStepSuffix(stepLibrary.getSimpleName());
        def humanizedName = NameConverter.humanize(normalizedName).toLowerCase()
        return NameConverter.underscore(humanizedName)
    }

    private static String stripStepSuffix(String classname) {
        if (classname.endsWith("Steps")) {
            return classname.substring(0, classname.length() - 5)
        } else {
            return classname
        }
    }
}

package net.thucydides.core.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.Screenshots;
import net.thucydides.core.webdriver.Configuration;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotPermission {

    private final Configuration configuration;

    public ScreenshotPermission(Configuration configuration) {
        this.configuration = configuration;
    }


    public boolean areAllowed(TakeScreenshots takeScreenshots) {
        Optional<TakeScreenshots> overrideLevel = methodOverride();
        if (overrideLevel.isPresent()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(overrideLevel.get());
        }

        if (configuration.onlySaveFailingScreenshots()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.FOR_FAILURES);
        }
        if (configuration.takeVerboseScreenshots()) {
            return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.FOR_EACH_ACTION);
        }
        return takeScreenshotLevel(takeScreenshots).isAtLeast(TakeScreenshots.FOR_EACH_STEP);
    }

    private Optional<TakeScreenshots> methodOverride() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            Method callingMethod = null;
            try {
                callingMethod = findMethodIn(stackTraceElement);
            } catch (ClassNotFoundException ignored) {}
            if (callingMethod != null && callingMethod.getAnnotation(Screenshots.class) != null) {
                return Optional.of(screenshotLevelFrom(callingMethod.getAnnotation(Screenshots.class)));
            }
        }
        return Optional.absent();
    }

    private TakeScreenshots screenshotLevelFrom(Screenshots screenshots) {
        if (screenshots.onlyOnFailures()) {
            return TakeScreenshots.FOR_FAILURES;
        } else if (screenshots.forEachAction()) {
            return TakeScreenshots.FOR_EACH_ACTION;
        } else if (screenshots.forEachStep()) {
            return TakeScreenshots.FOR_EACH_STEP;
        } else {
            return TakeScreenshots.FOR_EACH_STEP;
        }
    }


    private Method findMethodIn(StackTraceElement stackTraceElement) throws ClassNotFoundException {
        if (allowedClassName(stackTraceElement.getClassName())) {
            Class callingClass = Class.forName(stackTraceElement.getClassName());
            Method[] methods = ArrayUtils.addAll(callingClass.getDeclaredMethods(), callingClass.getMethods());
            for (Method method : methods) {
                if (stackTraceElement.getMethodName().equals(method.getName())) {
                    return method;
                }
            }
        }
        return null;
    }

    private boolean allowedClassName(String className) {
        return !((className.startsWith("sun.")) || (className.startsWith("java.")));
    }

    private TakeScreenshotsComparer takeScreenshotLevel(TakeScreenshots takeScreenshots) {
        return new TakeScreenshotsComparer(takeScreenshots);
    }

    private static class TakeScreenshotsComparer {
        private final TakeScreenshots takeScreenshots;

        private TakeScreenshotsComparer(TakeScreenshots takeScreenshots) {
            this.takeScreenshots = takeScreenshots;
        }

        public boolean isAtLeast(TakeScreenshots requiredLevel) {
            return takeScreenshots.compareTo(requiredLevel) >= 0;
        }
    }
}

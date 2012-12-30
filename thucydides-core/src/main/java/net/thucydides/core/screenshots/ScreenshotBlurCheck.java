package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.annotations.BlurScreenshots;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;

public class ScreenshotBlurCheck {


    public Optional<BlurLevel> blurLevel() {
        return fromAnnotation();
    }

    private Optional<BlurLevel> fromAnnotation() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            Method callingMethod = null;
            try {
                callingMethod = findMethodIn(stackTraceElement);
            } catch (ClassNotFoundException ignored) {}
            if (callingMethod != null && callingMethod.getAnnotation(BlurScreenshots.class) != null) {
                return Optional.of(BlurLevel.valueOf(callingMethod.getAnnotation(BlurScreenshots.class).value().toUpperCase()));
            }
        }
        return Optional.absent();
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
}

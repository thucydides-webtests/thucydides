package net.thucydides.junit.steps.junit;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.steps.StepResult;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/** 
 * Listen to step results and publish JUnit notification messages.
 * @author johnsmart
 *
 */
public class StepInterceptor implements MethodInterceptor {

    private final List<RunListener> listeners;
    private final Class<?> testStepClass;
    private StepResult resultTally;
    
    private boolean failureHasOccured = false;
    private Throwable error = null;

    public StepInterceptor(final Class<?> testStepClass, final List<RunListener> listeners) {
        this.testStepClass = testStepClass;
        this.listeners = listeners;
        this.failureHasOccured = false;
        this.resultTally = new StepResult();
    }

    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {

        if (invokingLast(method)) {
            notifyFinished(method);
            ifAnErrorOccuredThrow(error);
            return null;
        }
        
        if (!isATestStep(method)) {
            return invokeMethod(obj, method, args, proxy);
        }
        
        if (isPending(method) || isIgnored(method) ) {
            notifyTestSkippedFor(method, args);
            return null;
        }
        
        if (failureHasOccured) {
            notifyTestSkippedFor(method, args);
            return null;
        }
        
        return runTestStep(obj, method, args, proxy);
    }

    private boolean isATestStep(final Method method) {
        Step stepAnnotation = (Step) method.getAnnotation(Step.class);
        return (stepAnnotation != null);
    }

    private boolean isIgnored(final Method method) {
        Ignore ignoreAnnotation = (Ignore) method.getAnnotation(Ignore.class);
        return (ignoreAnnotation != null);
    }

    private Object runTestStep(final Object obj, final Method method, final Object[] args,
            final MethodProxy proxy) throws Throwable {
        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
            notifyTestFinishedFor(method, args);
        } catch (Throwable e) {
            error = e;
            notifyFailureOf(method, args, e);
            failureHasOccured = true;
        }
        resultTally.logExecutedTest();
        return result;
    }
    
    private Object invokeMethod(final Object obj, final Method method, final Object[] args,
            final MethodProxy proxy) throws Throwable {
        return proxy.invokeSuper(obj, args);
    }    

    private boolean isPending(final Method method) {
        Pending pendingAnnotation = (Pending) method.getAnnotation(Pending.class);
        return (pendingAnnotation != null);
    }

    private void notifyTestFinishedFor(final Method method, final Object[] args) throws Exception {
        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        for(RunListener listener : listeners) {
            listener.testFinished(description);
        }
    }

    private String getTestNameFrom(final Method method, final Object[] args) {
        if ((args == null) || (args.length == 0)) {
            return method.getName();
        } else {
            return testNameWithArguments(method, args);
        }
    }

    private String testNameWithArguments(final Method method, final Object[] args) {
        StringBuffer testName = new StringBuffer(method.getName());
        testName.append(": "); 
        boolean isFirst = true;
        for(Object arg: args) {
            if (!isFirst) {
                testName.append(", ");
            }
            testName.append(arg);
            isFirst = false;
        }
        return testName.toString();
    }

    private void notifyTestSkippedFor(final Method method, final Object[] args) throws Exception {
        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        for(RunListener listener : listeners) {
            listener.testIgnored(description);
        }
        resultTally.logIgnoredTest();
    }

    private void ifAnErrorOccuredThrow(final Throwable theError) throws Throwable {
        if (theError != null) {
            throw theError;
        }
    }

    private void notifyFailureOf(final Method method, final Object[] args, final Throwable e) throws Exception {
        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        Failure failure = new Failure(description, e);

        for(RunListener listener : listeners) {
            listener.testFailure(failure);
        }
        resultTally.logFailure(failure);
    }

    private void notifyFinished(final Method method) throws Exception {
        for(RunListener listener : listeners) {
            listener.testRunFinished(resultTally);
        }
    }

    private boolean invokingLast(final Method method) {
        return method.getName().equals("done");
    }

}

package net.thucydides.junit.steps;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;

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
    private List<Throwable> stepExceptions;
    private boolean failureHasOccured = false;
    private Throwable error = null;

    public StepInterceptor(final Class<?> testStepClass, final List<RunListener> listeners) {
        this.testStepClass = testStepClass;
        this.listeners = listeners;
        this.failureHasOccured = false;
        this.resultTally = new StepResult();
        this.stepExceptions = new ArrayList<Throwable>();
    }

    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {

        if (invokingLast(method)) {
            notifyFinished(method);
            ifAnErrorOccuredThrow(error);
            return null;
        }
        
        Object result = null;
        if (isATestGroup(method)) {     
            notifyGroupStarted(method, args);
            result = runTestGroupStep(obj, method, args, proxy);
            notifyGroupFinished(method, args);
        } else {
            result = testStepResult(obj, method, args, proxy);
        }
        return result;
        
    }

    private Object testStepResult(final Object obj, 
            final Method method, 
            final Object[] args,
            final MethodProxy proxy) throws Throwable {

        if (!isATestStep(method)) {
            return invokeMethod(obj, method, args, proxy);
        }
        
        notifyTestStarted(method, args);
        
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

    private Object runTestGroupStep(final Object obj, 
            final Method method, 
            final Object[] args,
            final MethodProxy proxy) throws Throwable {

        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
        } catch (Throwable e) {
            if (!stepExceptions.contains(e)) {
                throw e;
            }
        }
        return result;
    }

    private boolean isATestGroup(final Method method) {
        return (getTestGroupAnnotationFor(method) != null);
    }

    private StepGroup getTestGroupAnnotationFor(final Method method) {
        return method.getAnnotation(StepGroup.class);
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
            stepExceptions.add(e);
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

    private void notifyGroupStarted(final Method method, final Object[] args) throws Exception {

        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        for(RunListener listener : listeners) {
            listener.testStarted(description);
        }
    }

    private void notifyGroupFinished(final Method method, final Object[] args) throws Exception {

        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        for(RunListener listener : listeners) {
            listener.testFinished(description);
        }
    }

    private void notifyTestStarted(final Method method, final Object[] args) throws Exception {

        Description description = Description.createTestDescription(testStepClass, getTestNameFrom(method, args));
        for(RunListener listener : listeners) {
            listener.testStarted(description);
        }
    }

    private boolean invokingLast(final Method method) {
        return (method.getName().equals("done") || (method.getName().equals("finalize")));
    }

}

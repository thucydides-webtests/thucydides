package net.thucydides.junit.runners;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

class TestClassRunnerForInstanciatedTestCase extends ThucydidesRunner {
     private final int parameterSetNumber;
     private final Object instanciatedTest;

     TestClassRunnerForInstanciatedTestCase(final Object instanciatedTest,
                                            final int parameterSetNumber) throws InitializationError {
         super(instanciatedTest.getClass());
         this.instanciatedTest = instanciatedTest;
         this.parameterSetNumber = parameterSetNumber;
     }

     @Override
     public Object createTest() throws Exception {
         return instanciatedTest;
     }

     @Override
     protected String getName() {
         return QualifierFinder.forTestCase(instanciatedTest).getQualifier();
     }

     @Override
     protected String testName(final FrameworkMethod method) {
         return String.format("%s[%s]", method.getName(), parameterSetNumber);
     }

     @Override
     protected Statement classBlock(final RunNotifier notifier) {
         return childrenInvoker(notifier);
     }

 }
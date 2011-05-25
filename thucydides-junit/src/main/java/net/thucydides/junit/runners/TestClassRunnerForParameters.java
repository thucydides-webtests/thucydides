package net.thucydides.junit.runners;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

   class TestClassRunnerForParameters extends ThucydidesRunner {
        private final int fParameterSetNumber;
        private final List<Object[]> fParameterList;

        TestClassRunnerForParameters(final Class<?> type,
                                     final List<Object[]> parameterList,
                                     final int i) throws InitializationError {
            super(type);
            fParameterList = parameterList;
            fParameterSetNumber = i;
        }

        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(computeParams());
        }

        private Object[] computeParams() throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException cause) {
                throw new Exception(String.format(
                        "%s.%s() must return a Collection of arrays.",
                        getTestClass().getName(),
                        DataDrivenAnnotations.forClass(getTestClass()).getTestDataMethod().getName()),
                        cause);
            }
        }

        @Override
        protected String getName() {
            String firstParameter = fParameterList.get(fParameterSetNumber)[0].toString();
            return String.format("[%s]", firstParameter);
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return String.format("%s[%s]", method.getName(), fParameterSetNumber);
        }

        @Override
        protected void validateConstructor(final List<Throwable> errors) {
            validateOnlyOneConstructor(errors);
        }

        @Override
        protected Statement classBlock(final RunNotifier notifier) {
            return childrenInvoker(notifier);
        }

    }
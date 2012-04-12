package net.thucydides.junit.listeners;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.junit.runners.ParameterizedJUnitStepListener;

import java.io.File;

public class JUnitStepListenerBuilder {
    private final File outputDirectory;
    private final Pages pageFactory;
    private final int parameterSetNumber;


    public JUnitStepListenerBuilder(File outputDirectory) {
        this(outputDirectory, null, -1);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory) {
        this(outputDirectory, pageFactory, -1);
    }

    public JUnitStepListenerBuilder(File outputDirectory,
                                    Pages pageFactory,
                                    int parameterSetNumber) {
        this.outputDirectory = outputDirectory;
        this.pageFactory = pageFactory;
        this.parameterSetNumber = parameterSetNumber;
    }

    public JUnitStepListenerBuilder and() {
        return this;
    }

    public JUnitStepListenerBuilder withPageFactory(Pages pageFactory) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory);
    }

    public JUnitStepListenerBuilder withParameterSetNumber(int parameterSetNumber) {
        return new JUnitStepListenerBuilder(outputDirectory, pageFactory, parameterSetNumber);
    }

    public JUnitStepListener build() {
        if (parameterSetNumber >= 0) {
            return newParameterizedJUnitStepListener();
        } else {
            return newStandardJunitStepListener();
        }
    }

    private BaseStepListener buildBaseStepListener() {
        if (pageFactory != null) {
            return Listeners.getBaseStepListener()
                             .withPages(pageFactory)
                             .and().withOutputDirectory(outputDirectory);
        } else {
            return Listeners.getBaseStepListener()
                            .withOutputDirectory(outputDirectory);
        }
    }

    private JUnitStepListener newParameterizedJUnitStepListener() {
        return new ParameterizedJUnitStepListener(parameterSetNumber,
                                                  buildBaseStepListener(),
                                                  Listeners.getLoggingListener(),
                                                  Listeners.getStatisticsListener());
    }

    private JUnitStepListener newStandardJunitStepListener() {
        return new JUnitStepListener(buildBaseStepListener(),
                                     Listeners.getLoggingListener(),
                                     Listeners.getStatisticsListener());
    }

}
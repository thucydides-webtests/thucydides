package net.thucydides.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AggregateTestResults;
import net.thucydides.core.reports.xml.XMLAggregateTestReporter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generate aggregate XML acceptance test reports.
 * 
 * @goal aggregate
 * @requiresReports true
 * @phase verify
 */
public class ThucydidesReporterMojo extends AbstractMojo {
    /**
     * Aggregate reports are generated here
     * 
     * @parameter expression="${project.build.directory}/thucydides"
     * @required
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try {
            XMLAggregateTestReporter reporter = new XMLAggregateTestReporter();
            reporter.setOutputDirectory(outputDirectory);
            File sourceDirectory = outputDirectory;

            AggregateTestResults aggregateTestResults = reporter
                    .loadAllReportsFrom(sourceDirectory);
            reporter.setSourceDirectory(sourceDirectory);
            reporter.generateReportFor(aggregateTestResults);
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating aggregate thucydides reports", e);
        }
    }
}

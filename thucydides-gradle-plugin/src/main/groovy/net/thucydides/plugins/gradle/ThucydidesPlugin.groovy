package net.thucydides.plugins.gradle

import net.thucydides.core.reports.ResultChecker
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import org.gradle.api.Plugin
import org.gradle.api.Project

class ThucydidesPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("thucydides", ThucydidesPluginExtension)
        project.task('aggregate') << {
            logger.lifecycle("Generating Thucydides Reports for ${project.thucydides.projectKey} to directory $project.thucydides.outputDirectory")
            System.properties['thucydides.project.key'] = project.thucydides.projectKey
            def reporter = new HtmlAggregateStoryReporter(project.thucydides.projectKey)
            reporter.outputDirectory = new File(project.thucydides.outputDirectory)
            reporter.issueTrackerUrl = project.thucydides.issueTrackerUrl
            reporter.jiraUrl = project.thucydides.jiraUrl
            reporter.jiraProject = project.thucydides.jiraProject
            reporter.generateReportsForTestResultsFrom(new File(project.projectDir, project.thucydides.sourceDirectory))
        }

        project.task('checkOutcomes') << {
            def reportDir = new File(project.projectDir, project.thucydides.outputDirectory)
            logger.lifecycle("Checking Thucydides results for ${project.thucydides.projectKey} in directory $reportDir")
            def checker = new ResultChecker(reportDir)
            checker.checkTestResults()
        }

    }
}
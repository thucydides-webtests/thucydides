class ThucydidesPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("thucydides", ThucydidesPluginExtension)
        project.task('aggregate') << {
            println "Generating Thucydides Reports to directory $project.thucydides.outputDirectory"
            def reporter = new HtmlAggregateStoryReporter(project.thucydides.projectKey)
            reporter.outputDirectory = project.thucydides.outputDirectory
            reporter.issueTrackerUrl = project.thucydides.issueTrackerUrl
            reporter.jiraUrl = project.thucydides.jiraUrl
            reporter.jiraProject = project.thucydides.jiraProject
            reporter.generateReportsForTestResultsFrom(project.thucydides.sourceDirectory)
        }
    }
}
class ThucydidesPluginExtension {
    def String outputDirectory = 'target/site/thucydides'
    def String projectKey
    def String issueTrackerUrl
    def String jiraUrl
    def String jiraProject
    def String sourceDirectory = outputDirectory
}
package net.thucydides.core.screenshots

import net.thucydides.core.reports.TestOutcomeLoader
import spock.lang.Specification
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import com.google.common.io.Files

class WhenProcessingScreenshots extends Specification {

    def loader = new TestOutcomeLoader()
    File targetDirectory
    File sourceDirectory
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def setup() {
        targetDirectory = File.createTempFile("tmp","screenshots")
        targetDirectory.delete()
        targetDirectory.mkdir()
        targetDirectory.deleteOnExit()

        sourceDirectory = File.createTempFile("tmp","screenshot-source")
        sourceDirectory.delete()
        sourceDirectory.mkdir()
        sourceDirectory.deleteOnExit()

    }

    private File copySourceScreenshot(File sourceDirectory) {
        def screenshotsSourceDirectory = new File(Thread.currentThread().getContextClassLoader().getResource("screenshots").getPath());
        def sampleScreenshot = new File(screenshotsSourceDirectory, "amazon.png")
        def timestamp = System.currentTimeMillis()
        def screenshot = new File(sourceDirectory, "amazon-${timestamp}.png")
        Files.copy(sampleScreenshot, screenshot)
        return screenshot;
    }

    def "should process queued screenshots"() {
        given:
            def screenshotProcessor = new MultithreadScreenshotProcessor(environmentVariables)
        when:
            (1..100).each {
                def screenshotFile = copySourceScreenshot(sourceDirectory)
                def targetFile = new File(targetDirectory,"screenshot-${it}.png")
                screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotFile,targetFile))
            }
            screenshotProcessor.waitUntilDone()
        then:
            assert (screenshotProcessor.isEmpty())
            assert targetDirectory.list().size() == 100
    }

    def "should process queued screenshots when tests are run in parallel"() {
        given:
            def screenshotProcessor = new MultithreadScreenshotProcessor(environmentVariables)
        when:
            def thread = Thread.start {
                for( i in 1..5 ) {
                    (1..20).each {
                        def screenshotFile = copySourceScreenshot(sourceDirectory)
                        def targetFile = new File(targetDirectory,"screenshot-${i}-${it}.png")
                        screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotFile,targetFile))
                    }
                    screenshotProcessor.waitUntilDone()
                }
            }
            thread.join()

        then:
            assert (screenshotProcessor.isEmpty())
            assert targetDirectory.list().size() == 100
    }
}

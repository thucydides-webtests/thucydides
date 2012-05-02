package net.thucydides.core.screenshots

import net.thucydides.core.reports.TestOutcomeLoader
import spock.lang.Specification

class WhenProcessingScreenshots extends Specification {

    def loader = new TestOutcomeLoader()
    def screenshotData = new byte[10000]
    File targetDirectory

    def setup() {
        targetDirectory = File.createTempFile("tmp","screenshots")
        targetDirectory.delete()
        targetDirectory.mkdir()
        targetDirectory.deleteOnExit()
    }

    def "should process queued screenshots"() {
        given:
        def screenshotProcessor = new MultithreadScreenshotProcessor()
        when:
            (1..1000).each {
                screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotData,
                        new File(targetDirectory,"screenshot-${it}.png")))
            }
            screenshotProcessor.waitUntilDone()
        then:
            assert (screenshotProcessor.isEmpty())
            assert targetDirectory.list().size() == 1000
    }

    def "should process queued screenshots when tests are run in parallel"() {
        given:
            def screenshotProcessor = new MultithreadScreenshotProcessor()
        when:
            def thread = Thread.start {
                for( i in 1..5 ) {
                    (1..50).each {
                        screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotData,
                                new File(targetDirectory,"screenshot-${i}-${it}.png")))
                    }
                    screenshotProcessor.waitUntilDone()
                }
            }
            thread.join()

        then:
            assert (screenshotProcessor.isEmpty())
            assert targetDirectory.list().size() == 250
    }
}

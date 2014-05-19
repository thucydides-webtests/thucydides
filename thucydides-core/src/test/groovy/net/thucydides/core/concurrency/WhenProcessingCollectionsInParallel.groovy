package net.thucydides.core.concurrency

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

class WhenProcessingCollectionsInParallel extends Specification {

    def "should process collections concurrently"() {
        given:
        def list = [1,2,3,4,5,6,7,8,9,10]
        AtomicInteger total = new AtomicInteger(0);

        when:
        Parallel.blockingFor(list, new Parallel.Operation<Integer>() {
            @Override
            void perform(Integer value) {
                total.addAndGet(value);
            }
        })

        then:
        total.get() == 55
    }

    def "should be able to specify the number of threads"() {
        given:
        def list = [1,2,3,4,5,6,7,8,9,10]
        AtomicInteger total = new AtomicInteger(0);

        when:
        Parallel.blockingFor(4, list, new Parallel.Operation<Integer>() {
            @Override
            void perform(Integer value) {
                total.addAndGet(value
                );
            }
        })

        then:
        total.get() == 55
    }

}
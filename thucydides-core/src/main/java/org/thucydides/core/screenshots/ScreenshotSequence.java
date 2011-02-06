package org.thucydides.core.screenshots;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple sequencer for screenshot numbers.
 * @author johnsmart
 *
 */
public class ScreenshotSequence {
    private AtomicLong sequenceNumber = new AtomicLong(1);

    public long next() {
        return sequenceNumber.getAndIncrement();
    }
}

package net.thucydides.jbehave;

import net.thucydides.jbehave.JUnitThucydidesStories;

public class AStorySample extends JUnitThucydidesStories {
    private final String storyName;

    public AStorySample(String storyName) {
        this.storyName = storyName;
    }

    public void configure() {
        findStoriesCalled(storyName);
    }
}


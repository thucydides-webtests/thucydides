package net.thucydides.core.fixtureservices;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface FixtureService {
    void setup() throws Exception;
    void shutdown() throws Exception;
    void addCapabilitiesTo(DesiredCapabilities capabilities);
}

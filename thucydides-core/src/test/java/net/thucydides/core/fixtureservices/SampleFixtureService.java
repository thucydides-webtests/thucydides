package net.thucydides.core.fixtureservices;

import net.thucydides.browsermob.fixtureservices.FixtureService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SampleFixtureService implements FixtureService {
    @Override
    public void setup() throws Exception {
    }

    @Override
    public void shutdown() throws Exception {
    }

    @Override
    public void addCapabilitiesTo(DesiredCapabilities capabilities) {
    }
}

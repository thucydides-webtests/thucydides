package net.thucydides.browsermob.fixtureservices;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.UnknownHostException;
import java.util.List;

public class BrowserMobFixtureService implements FixtureService {

    private static int DEFAULT_PORT = 5555;

    private final EnvironmentVariables environmentVariables;

    private ProxyServer proxyServer;

    public BrowserMobFixtureService() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public BrowserMobFixtureService(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public void setup() throws Exception {
        if (useBrowserMobProxyManager()) {
            initializeProxy(getBrowserMobProxyPort());
        }
    }

    private void initializeProxy(int port) throws Exception {
        proxyServer = new ProxyServer(port);
        proxyServer.start();
    }

    @Override
    public void shutdown() throws Exception {
        if (proxyServer != null) {
            proxyServer.stop();
            proxyServer = null;
        }
    }

    @Override
    public void addCapabilitiesTo(DesiredCapabilities capabilities) {
        try {
            capabilities.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean useBrowserMobProxyManager() {
        String browserMobFilter = environmentVariables.getProperty(BrowserMobSystemProperties.BROWSER_MOB_FILTER.getName());
        return (StringUtils.isEmpty(browserMobFilter) || shouldActivateBrowserMobWithDriver(browserMobFilter, environmentVariables));
    }

    private boolean shouldActivateBrowserMobWithDriver(String filter, EnvironmentVariables environmentVariables) {
        String currentDriver = environmentVariables.getProperty(ThucydidesSystemProperty.DRIVER);
        List allowedBrowsers = Lists.newArrayList(Splitter.on(",").trimResults().split(filter.toLowerCase()));
        return StringUtils.isEmpty(currentDriver) || allowedBrowsers.contains(currentDriver.toLowerCase());
    }

    public int getBrowserMobProxyPort() {
        return environmentVariables.getPropertyAsInteger(BrowserMobSystemProperties.BROWSER_MOB_PROXY, DEFAULT_PORT);
    }
}

package net.thucydides.core.guice;

import com.google.inject.Inject;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Rahul
 * Date: 6/13/12
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class EclipseLinkEnvironmentVariablesConfig implements JPAProviderConfig {

    private final EnvironmentVariables environmentVariables;
    private final LocalDatabase localDatabase;
    private boolean isActive = true;

    @Inject
    public EclipseLinkEnvironmentVariablesConfig(EnvironmentVariables environmentVariables,
                                               LocalDatabase localDatabase) {
        this.environmentVariables = environmentVariables;
        this.localDatabase = localDatabase;
    }

    @Override
    public void setProperties(Properties properties) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

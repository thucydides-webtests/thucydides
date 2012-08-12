package net.thucydides.core.jpa;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.statistics.database.LocalDatabase;
import net.thucydides.core.util.EnvironmentVariables;

/**
 * Created with IntelliJ IDEA.
 * User: Rahul
 * Date: 6/7/12
 * Time: 7:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class JPAProviderConfigFactory {

    public static final JPAProvider DEFAULT_PROVIDER = JPAProvider.Hibernate;
    public static JPAProviderConfig getJPAProviderConfig(EnvironmentVariables environmentVariables,
                                                         LocalDatabase localDatabase) {
        String providerProperty =  ThucydidesSystemProperty.JPA_PROVIDER.from(environmentVariables, DEFAULT_PROVIDER.name());
        JPAProvider provider =  getJPAProviderFromProperty(providerProperty);
        return getJPAProviderConfig(provider, environmentVariables, localDatabase);


    }

    private static JPAProvider getJPAProviderFromProperty(String providerProperty) {

        JPAProvider provider = JPAProvider.Hibernate;

        try {
            provider   = JPAProvider.valueOf(providerProperty);
        }catch(IllegalArgumentException iae) {
            //invalid provider property, use default
        }

        return provider;
    }

    private static JPAProviderConfig getJPAProviderConfig(JPAProvider provider, EnvironmentVariables environmentVariables,
                                                          LocalDatabase localDatabase) {
        switch (provider) {
            case EclipseLink: return new EclipseLinkEnvironmentVariablesConfig(environmentVariables, localDatabase);
            default: return new HibernateEnvironmentVariablesConfig(environmentVariables,localDatabase);
        }
    }


}
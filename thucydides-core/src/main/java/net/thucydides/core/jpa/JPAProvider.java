package net.thucydides.core.jpa;

/**
 * Created with IntelliJ IDEA.
 * User: Rahul
 * Date: 6/13/12
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public enum JPAProvider {

    Hibernate ("db-manager"),
    OpenJPA("db-manager-OpenJPA");

    private String persistenceUnit;

    JPAProvider(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public String getPersistenceUnitName() {
        return this.persistenceUnit;
    }



}

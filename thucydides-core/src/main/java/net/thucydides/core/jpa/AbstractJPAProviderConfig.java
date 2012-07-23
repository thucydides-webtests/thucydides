package net.thucydides.core.jpa;

import net.thucydides.core.util.EnvironmentVariables;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rahul
 * Date: 7/1/12
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractJPAProviderConfig implements JPAProviderConfig {

    private static final int TABLE_NAME_COLUMN  = 3;

    protected List<String> getTablesFrom(Connection conn) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        List<String> tableNames = new ArrayList<String>();
        while (rs.next()) {
            tableNames.add(rs.getString(TABLE_NAME_COLUMN));
        }
        return tableNames;
    }

    protected boolean isUsingLocalDatabase(EnvironmentVariables environmentVariables) {
        return (environmentVariables.getProperty("thucydides.statistics.url") == null);
    }

}

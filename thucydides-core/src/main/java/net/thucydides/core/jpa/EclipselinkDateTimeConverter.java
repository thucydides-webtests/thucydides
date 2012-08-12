package net.thucydides.core.jpa;

import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rahul
 * Date: 7/24/12
 * Time: 5:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class EclipselinkDateTimeConverter implements Converter {

    public Object convertDataValueToObjectValue(Object dataValue, Session arg1) {
        if (dataValue instanceof Timestamp)
            return new DateTime(dataValue);
        else
            throw new IllegalStateException("Conversion exception, value is not of Timestamp type.");

    }

    public Object convertObjectValueToDataValue(Object objectValue, Session arg1) {
        if (objectValue instanceof DateTime) {
            return new Timestamp(((DateTime) objectValue).getMillis());
        } else
            throw new IllegalStateException("Conversion exception, value is not of DateTime type.");

    }

    public void initialize(DatabaseMapping arg0, Session arg1) {
    }

    public boolean isMutable() {
        return false;
    }

}


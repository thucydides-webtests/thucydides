package net.thucydides.plugins.jira.client;

import javax.xml.rpc.ServiceException;

/**
 * The JIRA SOAP service could not be located.
 */
public class CouldNotLocateServiceException extends RuntimeException {
    public CouldNotLocateServiceException(String message, ServiceException cause) {
        super(message, cause);
    }
}

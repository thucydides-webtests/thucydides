package net.thucydides.plugins.jira.client;

import org.apache.axis.AxisFault;
import thucydides.plugins.jira.soap.JiraSoapService;
import thucydides.plugins.jira.soap.JiraSoapServiceService;
import thucydides.plugins.jira.soap.JiraSoapServiceServiceLocator;

import javax.naming.ServiceUnavailableException;
import javax.xml.rpc.ServiceException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * This represents a SOAP session with JIRA including that state of being logged in or not.
 */
public class SOAPSession
{
    private JiraSoapServiceService jiraSoapServiceLocator;
    private JiraSoapService jiraSoapService;
    private String token;

    /**
     * Open a connection to the specified JIRA SOAP web service.
     * @param webServicePort
     */
    public static SOAPSession openConnectionTo(final URL webServicePort) {
        return new SOAPSession(webServicePort);
    }

    protected JiraSoapServiceService getServiceLocator() {
        if (jiraSoapServiceLocator == null) {
            jiraSoapServiceLocator = new JiraSoapServiceServiceLocator();
        }
        return jiraSoapServiceLocator;
    }

    protected SOAPSession(final URL webServicePort)
    {
        try
        {
            jiraSoapService = getServiceLocator().getJirasoapserviceV2(webServicePort);
        }
        catch (ServiceException e)
        {
            throw new CouldNotLocateServiceException("ServiceException during SOAPClient contruction", e);
        }
    }

    public SOAPSession usingCredentials(String userName, String password) throws RemoteException
    {
        token = getJiraSoapService().login(userName, password);
        return this;
    }

    public String getAuthenticationToken()
    {
        return token;
    }

    public JiraSoapService getJiraSoapService()
    {
        return jiraSoapService;
    }
}

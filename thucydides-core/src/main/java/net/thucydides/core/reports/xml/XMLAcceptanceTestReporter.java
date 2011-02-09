package net.thucydides.core.reports.xml;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;

public class XMLAcceptanceTestReporter implements AcceptanceTestReporter {

    public File generateReportFor(AcceptanceTestRun testRun) throws IOException {

        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
        xstream.registerConverter(new AcceptanceTestRunConverter());
        String xmlContents = xstream.toXML(testRun);
        System.out.println(xmlContents);
        
        File report = new File("target/thucydides/thucydides.xml");
        FileUtils.writeStringToFile(report, xmlContents);
        
        return report;
    }

}

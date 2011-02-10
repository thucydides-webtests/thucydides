package net.thucydides.core.reports.xml;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;

public class XMLAcceptanceTestReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    private File outputDirectory;
    
    public File generateReportFor(AcceptanceTestRun testRun) throws IOException {

        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
        xstream.registerConverter(new AcceptanceTestRunConverter());
        String xmlContents = xstream.toXML(testRun);
        System.out.println(xmlContents);
        
        File report = new File(getOutputDirectory(),"thucydides.xml");
        FileUtils.writeStringToFile(report, xmlContents);
        
        return report;
    }

    public String getOutputDirectory() {
        if (outputDirectory == null) {
            outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY);
        }
        return null;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}

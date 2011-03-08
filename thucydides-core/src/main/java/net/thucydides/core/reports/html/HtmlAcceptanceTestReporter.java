package net.thucydides.core.reports.html;

import static net.thucydides.core.reports.ReportNamer.ReportType.HTML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.regex.Pattern;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportNamer;
import net.thucydides.core.resources.ResourceList;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.base.Preconditions;

/**
 * Generates acceptance test results in XML form.
 * TODO: Tidy up this class. 
 * 
 */
public class HtmlAcceptanceTestReporter implements AcceptanceTestReporter {

    private static final int BUFFER_SIZE = 4096;

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";

    private File outputDirectory;

    private ReportNamer reportNamer = new ReportNamer(HTML);

    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;

    private VelocityEngine ve = new VelocityEngine();
    
    public HtmlAcceptanceTestReporter() {
        ve.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        ve.addProperty("classpath." + Velocity.RESOURCE_LOADER + ".class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        ve.init();
    }

    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final AcceptanceTestRun testRun) throws IOException {

        Preconditions.checkNotNull(outputDirectory);

        String htmlContents = "";
        Template template = getTemplate();
        VelocityContext context = new VelocityContext();
        context.put("testrun", testRun);
        StringWriter sw = new StringWriter();
        template.merge(context, sw);
        htmlContents = sw.toString();

        String reportFilename = reportNamer.getNormalizedTestNameFor(testRun);
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);

        copyHTMLResourcesTo(getOutputDirectory());

        return report;
    }

    /**
     * Resources (stylesheets, images) etc are all stored in the
     * src/main/resources/reports directory. When the jar is deployed, they will
     * end up on the classpath.
     */
    private void copyHTMLResourcesTo(final File targetDirectory) throws IOException {

        Pattern resourcePattern = allFilesInDirectory(resourceDirectory);
        Collection<String> reportResources = ResourceList.getResources(resourcePattern);
        for (String resourcePath : reportResources) {
            String targetSubDirectory = findTargetSubDirectoryFrom(resourcePath, targetDirectory);
            File copyTo = new File(targetDirectory, targetSubDirectory);
            if (resourceIsFromAJar(resourcePath) 
                && (thisIsNotTheRoot(resourcePath)) 
                && (thisIsNotADirectory(resourcePath))) {
                copyFileFromClasspathToTargetDirectory(resourcePath, copyTo);
            }
        }
    }

    private boolean thisIsNotADirectory(final String resourcePath) {
        return !resourcePath.endsWith("/");
    }

    private boolean thisIsNotTheRoot(final String resourcePath) {
        return !resourceDirectory.equals(resourcePath);
    }

    private String findTargetSubDirectoryFrom(final String resourcePath, final File inTargetDirectory) throws IOException {
        File targetDirectory = inTargetDirectory;
        if (resourcePath.startsWith(resourceDirectory)) {
            int subDirectoryStartsAt = resourceDirectory.length() + 1;
            String resourcePathName = resourcePath.substring(subDirectoryStartsAt);
            if (resourcePathName.endsWith("/")) {
                return resourcePathName;
            } else {
                targetDirectory = new File(resourcePathName).getParentFile();
                if (targetDirectory != null) {
                    return targetDirectory.getPath();
                }
            }
        }
        return "";
    }

    private Pattern allFilesInDirectory(final String directory) {
        return Pattern.compile(".*" + directory + "/.*");
   }

    private void copyFileToTargetDirectory(final String resourcePath, final File targetDirectory)
            throws IOException {
        FileUtils.copyFileToDirectory(new File(resourcePath), targetDirectory);
    }

    private void copyFileFromClasspathToTargetDirectory(final String resourcePath,
            final File targetDirectory) throws IOException {
        
        File resourceOnClasspath = new File(resourcePath);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        File destinationFile = new File(targetDirectory, resourceOnClasspath.getName());
        if (destinationFile.getParent() != null) {
            new File(destinationFile.getParent()).mkdirs();
        }
        FileOutputStream out = new FileOutputStream(destinationFile);
        byte[] buffer = new byte[BUFFER_SIZE];  
        int bytesRead;  
        while ((bytesRead = in.read(buffer)) != -1) {  
            out.write(buffer, 0, bytesRead);  
        }  
        in.close();  
        out.close(); 
    }

    private boolean resourceIsFromAJar(final String resourcePath) {
        return !resourcePath.startsWith("/");
    }

    private Template getTemplate() {
        return ve.getTemplate("velocity/default.vm");
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

}

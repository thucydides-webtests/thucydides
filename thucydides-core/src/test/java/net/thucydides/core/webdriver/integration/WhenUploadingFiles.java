package net.thucydides.core.webdriver.integration;

import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.components.FileToUpload;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WhenUploadingFiles {

    public class UploadPage extends PageObject {

        @FindBy(name = "upload")
        public WebElement uploadField;

        public UploadPage(WebDriver driver) {
            super(driver);
        }

        public void uploadFile(String filename) {
            upload(filename).to(uploadField);
        }
    }

    private static WebDriver driver;

    @BeforeClass
    public static void open_local_static_site() {
        driver = new FirefoxDriver();
        openStaticTestSite(driver);
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    private static void openStaticTestSite(WebDriver driver) {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }


    @Test
    public void should_upload_a_file_from_the_resources_directory() {
        UploadPage uploadPage = new UploadPage(driver);

        uploadPage.uploadFile("uploads/readme.txt");

        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("readme.txt"));

    }

    @Test
    public void should_upload_a_file_from_the_classpath() {
        UploadPage uploadPage = new UploadPage(driver);

        uploadPage.uploadFile("/uploads/readme.txt");

        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("readme.txt"));

    }

    @Test
    public void should_leave_a_unix_java_path_alone_when_running_on_unix() {
        String unixPath = "/home/myuser/target/test-classes/documentUpload/somefile.pdf";
        if (!runningOnWindows()) {
            WebElement field = mock(WebElement.class);

            FileToUpload fileToUpload = new FileToUpload(unixPath);
            fileToUpload.to(field);

            verify(field).sendKeys(unixPath);
        }
    }

    @Test
    public void should_leave_a_windows_java_path_alone_when_running_on_windows() {
        String windowsPath = "C:\\Users\\Joe Blogs\\Documents\\somefile.pdf";
        if (runningOnWindows()) {
            WebElement field = mock(WebElement.class);

            FileToUpload fileToUpload = new FileToUpload(windowsPath);
            fileToUpload.to(field);

            verify(field).sendKeys(windowsPath);
        }
    }

    private boolean runningOnWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void should_recognize_a_simple_windows_path() {
        assertThat(FileToUpload.isAFullWindowsPath("C:\\Projects\\somefile.pdf"), is(true));
    }

    @Test
    public void should_recognize_a_unix_path() {
        assertThat(FileToUpload.isAFullWindowsPath("C:\\Projects\\somefile.pdf"), is(true));
    }

    @Test
    public void should_recognize_a_complex_windows_path() {
        assertThat(FileToUpload.isAFullWindowsPath("/home/myuser/target/test-classes/documentUpload/somefile.pdf"), is(false));
    }

    @Test
    public void should_upload_a_relative_path_from_the_current_working_directory() throws IOException {

        File currentDirectory = new File(System.getProperty("user.dir"));
        File targetDirectory = new File(currentDirectory, "target");
        File uploadedFile = new File(targetDirectory, "upload.txt");
        writeTextToFile("Hi there", uploadedFile);

        UploadPage uploadPage = new UploadPage(driver);

        uploadPage.uploadFile("target/upload.txt");

        assertThat(uploadPage.uploadField.getAttribute("value"), containsString("upload.txt"));
    }

    private void writeTextToFile(String text, File uploadedFile) throws IOException {
        PrintWriter out = new PrintWriter(uploadedFile);
        out.close();
    }

}

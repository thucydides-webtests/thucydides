package net.thucydides.core.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenReadingResourcesFromTheClasspath {

    @Test
    public void should_return_a_list_of_resources_on_the_classpage() {
        Pattern pattern = Pattern.compile(".*");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.isEmpty(), is(false));
    }

    @Test
    public void should_return_a_list_of_resources_in_a_given_package() {
        Pattern pattern = Pattern.compile(".*[\\\\/]resourcelist[\\\\/].*");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.size(), greaterThan(0));
    }
    
    @Test
    public void should_return_a_list_of_resources_in_a_given_package_containing_matching_resources() {
        Pattern pattern = Pattern.compile(".*[\\\\/]resourcelist[\\\\/].*");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.size(), greaterThan(0));
        assertThat(resources, hasItems(containsString("resourcelist"),endsWith("sample.css"),endsWith("sample.xsl")));
    }

    @Test
    public void should_return_a_list_of_resources_in_a_given_package_even_from_a_dependency() {
        Pattern pattern = Pattern.compile(".*/findElement.js");
        Collection<String> resources = ResourceList.getResources(pattern);
        assertThat(resources.isEmpty(), is(false));
    }

    @Test
    public void should_transform_windows_source_path_into_relative_target_path() {

        String sourceResource = "C:\\Projects\\thucydides\\thucydides-report-resources\\target\\classes\\report-resources\\css\\core.css";

        String expectedTargetSubDirectory = "css";
        FileResources fileResource = FileResources.from("report-resources");
        String targetSubdirectory = fileResource.findTargetSubdirectoryFrom(sourceResource);
        assertThat(targetSubdirectory, is(expectedTargetSubDirectory));
    }

    @Test
    public void should_handle_a_nested_resource_directory() {

        String sourceResource = "C:\\Projects\\thucydides\\thucydides-report-resources\\target\\classes\\report-resources\\css\\core.css";

        String expectedTargetSubDirectory = "css";
        FileResources fileResource = FileResources.from("classes\\report-resources");
        String targetSubdirectory = fileResource.findTargetSubdirectoryFrom(sourceResource);
        assertThat(targetSubdirectory, is(expectedTargetSubDirectory));
    }


    @Test
    public void should_transform_unix_source_path_into_relative_target_path_without_subdirectory() {

        String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/core.css";

        String expectedTargetSubDirectory = "";

        FileResources fileResource = FileResources.from("report-resources");
        assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
    }

    @Test
     public void should_handle_nested_subdirectories_in_unix() {

         String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/css/core.css";

         String expectedTargetSubDirectory = "css";

         FileResources fileResource = FileResources.from("classes/report-resources");
         assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
     }

    @Test
    public void should_transform_unix_source_path_into_relative_target_path() {

        String sourceResource = "/Projects/thucydides/thucydides-report-resources/target/classes/report-resources/css/core.css";
        String resourceDirectory = "report-resources";

        String expectedTargetSubDirectory = "css";
        String expectedTargetFilename = "core.css";
        FileResources fileResource = FileResources.from("report-resources");

        assertThat(fileResource.findTargetSubdirectoryFrom(sourceResource), is(expectedTargetSubDirectory));
    }

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    @Test
    public void should_copy_resource_file_into_target_directory() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/sample.css").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(targetDir, "sample.css");
        assertThat(destinationFile.exists(), is(true));
    }

    @Test
    public void should_copy_resource_file_into_nested_target_directory() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/stylesheets/sample.css").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(new File(targetDir,"stylesheets"), "sample.css");
        assertThat(destinationFile.exists(), is(true));
    }

    @Test
    public void should_create_directory_if_copied() throws Exception {
        File targetDir = temporaryDirectory.newFolder("target");
        String sourceResource = new File("src/test/resources/resourcelist/stylesheets").getAbsolutePath();
        FileResources fileResource = FileResources.from("resourcelist");

        fileResource.copyResourceTo(sourceResource, targetDir);

        File destinationFile = new File(targetDir,"stylesheets");
        assertThat(destinationFile.exists(), is(true));
        assertThat(destinationFile.isDirectory(), is(true));
    }

}

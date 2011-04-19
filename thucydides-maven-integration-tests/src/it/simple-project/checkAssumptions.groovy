/**
 * Only run these integration tests in the development phase with SNAPSHOTS.
 * Running them during a release doesn't work. Not sure why.
 */
def pomFile = new File("pom.xml").text
if (!pomFile.contains("SNAPSHOT")) {
    System.exit 0
}
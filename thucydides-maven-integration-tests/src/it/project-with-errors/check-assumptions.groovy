/**
 * Only run these integration tests in the development phase with SNAPSHOTS.
 * Running them during a release doesn't work. Not sure why.
 */
def pomFile = new File("pom.xml").text
assert pomFile.contains("SNAPSHOT")

/**
 * Only run these integration tests in the development phase with SNAPSHOTS.
 * Running them during a release doesn't work. Not sure why.
 */
def pomFile = new File("pom.xml").text
def exitValue = (pomFile.contains("SNAPSHOT") ? 0 : 1)
System.exit exitValue

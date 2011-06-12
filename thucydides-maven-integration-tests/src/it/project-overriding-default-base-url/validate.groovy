def buildLog = new File("${basedir}/build.log").text
assert buildLog.contains("Opening http://www.wikipedia.org")
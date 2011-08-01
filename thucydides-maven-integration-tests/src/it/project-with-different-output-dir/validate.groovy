// Verify story printing
htmlReport = new File("${basedir}/target/out/index.html")
assert htmlReport.exists()

htmlStoriesReport = new File("${basedir}/target/out/stories.html")
assert htmlStoriesReport.exists()
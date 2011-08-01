htmlReport = new File("${basedir}/target/thucydides/index.html")
assert htmlReport.exists()

storiesReport = new File("${basedir}/target/thucydides/stories.html")
assert storiesReport.exists()
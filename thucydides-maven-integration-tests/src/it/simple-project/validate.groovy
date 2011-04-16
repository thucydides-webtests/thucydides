htmlReport = new File("${basedir}/target/thucydides/home.html")
assert htmlReport.exists()

storiesReport = new File("${basedir}/target/thucydides/stories.html")
assert storiesReport.exists()
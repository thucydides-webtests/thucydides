scenario1XmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list.xml")
assert scenario1XmlReport.exists()

scenario2XmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list_again.xml")
assert scenario2XmlReport.exists()

scenario3XmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list_using_steps.xml")
assert scenario3XmlReport.exists()

scenario1HtmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list.html")
assert scenario1HtmlReport.exists()

scenario2HtmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list_again.html")
assert scenario2HtmlReport.exists()

scenario3HtmlReport = new File("${basedir}/target/thucydides/testingasimplesitewithafailingtest_select_entry_in_dropdown_list_using_steps.html")
assert scenario3HtmlReport.exists()

easybReport = new File("${basedir}/target/easyb/easyb.html")
assert easybReport.exists()


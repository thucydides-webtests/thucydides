package net.thucydides.easyb.samples

import net.thucydides.easyb.samples.pages.DemoSiteSteps

using "thucydides"

thucydides.uses_default_base_url "classpath:demosite/index.html"
thucydides.uses_steps_from DemoSiteSteps
thucydides.uses_driver "htmlunit"

tags "someTag"

/**
 * Thucydides can manage pages for us.
 */

scenario "Select entry in dropdown list", {
    given "we are on the Thucydides demo site"
    when "the user selects the 'Label 2' option"
    then "this option should be selected"
}

scenario "Select entry in dropdown list again", {
    given "we are still on the Thucydides demo site"
    when "the user selects the 'Label 1' option"
    then "this option should be selected"
}

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again"
    when "the user fills in the form"
    then "the chosen options should be displayed"
}

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


scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.enter_values('Label 3', true)
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '3'
    }
}


scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.enter_values('Label 3', true)
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '4'
    }
}

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.call_nested_steps("Label 1", true)
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '3'
    }
}

package net.thucydides.easyb.samples

import net.thucydides.easyb.samples.pages.DemoSiteSteps
import net.thucydides.easyb.samples.pages.IndexPage

using "thucydides"

thucydides.uses_default_base_url "classpath:demosite/index.html"
thucydides.uses_steps_named("a_developer").from DemoSiteSteps
thucydides.uses_driver "htmlunit"
thucydides.testing.behavior "Doing something else"

tags "someTag"

/**
 * Thucydides can manage pages for us.
 */

scenario "Select entry in dropdown list", {
    given "we are on the Thucydides demo site", {
        indexPage = pages.currentPageAt(IndexPage)
    }
    when "the user selects the 'Label 2' option", {
       indexPage.selectItem 'Label 2'
    }
    then "this option should be selected",{
       indexPage.selectedValues.shouldHave '2'
    }
}

scenario "Select entry in dropdown list again", {
    given "we are still on the Thucydides demo site", {
        indexPage = pages.currentPageAt(IndexPage)
    }
    when "the user selects the 'Label 1' option", {
        indexPage.selectItem 'Label 1'
    }
    then "this option should be selected",{
        indexPage.selectedValues.shouldHave '1'
    }
}

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        try {
            a_developer.enter_values('Label 3', true)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
    then "the chosen options should be displayed", {
        a_developer.should_have_selected_value('3')
    }
}


scenario "Select entry in dropdown list using nested steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        a_developer.use_nested_steps()
    }
    then "the chosen options should be displayed", {
        a_developer.should_have_selected_value '1'
    }
}

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        a_developer.enter_values('Label 3', true)
    }
    then "the chosen options should be displayed", {
        a_developer.should_have_selected_value '3'
    }
}

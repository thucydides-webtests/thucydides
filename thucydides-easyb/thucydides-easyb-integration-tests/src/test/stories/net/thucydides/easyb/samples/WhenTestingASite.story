package net.thucydides.easyb.samples

import net.thucydides.easyb.samples.pages.IndexPage
import net.thucydides.easyb.samples.pages.DemoSiteSteps;

using "thucydides"

thucydides.uses_default_base_url "classpath:demosite/index.html"
thucydides.uses_steps_from DemoSiteSteps

//thucydides.testsUserStory UserStories.Whatever, in(Features.Whatever)

tags "someTag"

/**
 * Thucydides can manage pages for us.
 */
       /*
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
       */
scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        try {
         demo_site.enter_values('Label 3', true)
        } catch(Exception e) {
            e.printStackTrace()
        }
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value('3')
    }
}


scenario "Select entry in dropdown list using nested steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.use_nested_steps()
    }
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '3'
    }
}

scenario "Select another entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again", {
    }
    when "the user fills in the form", {
        demo_site.enter_values('Label 3', true)
    }
    and "another step is pending"
    then "the chosen options should be displayed", {
        demo_site.should_have_selected_value '3'
    }
    and "this should be skipped", {
        demo_site.should_have_selected_value '3'
    }
    and "so should this one", {
        demo_site.should_have_selected_value '3'
    }
    and "this one", {
        demo_site.should_have_selected_value '3'
    }
}

/*
scenario "Triggering a failure", {
    given "we are on the Thucydides demo site again", {
    }
    when "the ussr triggers an assertion error", {
        demo_site.trigger_exception()
    }
    then "the broswer should still be closed", {
    }
}

*/

/**
* Or you can break functional tests into steps and step groups.
* This makes the net.thucydides.easyb story a high-level (business-friendly) acceptance test,
* with the details hidden in the steps. Steps can be 
*/
//
//scenario "Looking for dogs in Google and navigating to Wikipedia", {
//   given "a browser open at the Google home page"
//   when "the user searchs for 'Dogs'", {
//      google_search.search_for "Dogs"
//   }
//   and "the user clicks on the Wikipedia entry", {
//      google_search.click_on_search_result_with_title "Dog - Wikipedia, the free encyclopedia"
//   }
//   then "the Dogs entry in Wikipedia should appear on the first page", {
//   }
//}

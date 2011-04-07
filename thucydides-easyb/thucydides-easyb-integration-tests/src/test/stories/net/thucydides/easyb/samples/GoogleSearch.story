package net.thucydides.easyb.samples

import org.openqa.selenium.By
import net.thucydides.easyb.samples.pages.GoogleSearchPage
import net.thucydides.easyb.samples.pages.GoogleSearchResultsPage
import net.thucydides.easyb.samples.pages.GoogleSearchSteps;

using "thucydides"

thucydides.uses_default_base_url "http://www.google.com"
thucydides.uses_steps_from GoogleSearchSteps

/**
 * In the simplest case, Thucydides is simply used to manage a WebDriver instance.
 * This instance is injected into the story context under the name of 'driver'
 */
scenario "Looking for cats in Google", {
    given "a browser open at the Google home page", {
        driver.get("http://www.google.com")
    }
    when "the user searchs for 'Cats'", {
        driver.findElement(By.name("q")).sendKeys("cats")
        driver.findElement(By.name("btnG")).click()
        Thread.sleep 500
    }
    then "the Cats entry in Wikipedia should appear on the first page", {
        driver.findElement(By.xpath("//h3[.='Cat - Wikipedia, the free encyclopedia']"))
    }
}

/**
 * Thucydides can also manage pages for us.
 */

scenario "Looking for dogs in Google", {
    given "a browser open at the Google home page", {
        searchPage = pages.currentPageAt(GoogleSearchPage)
    }
    when "the user searchs for 'Dogs'", {
        searchPage.searchFor "Dogs"
    }
    then "the Dogs entry in Wikipedia should appear on the first page", {
        resultsPage = pages.currentPageAt(GoogleSearchResultsPage)
        resultsPage.topicTitles.shouldHave "Dog - Wikipedia, the free encyclopedia"
    }
}

/**
* Or you can break functional tests into steps and step groups.
* This makes the net.thucydides.easyb story a high-level (business-friendly) acceptance test,
* with the details hidden in the steps. Steps can be 
*/

scenario "Looking for dogs in Google and navigating to Wikipedia", {
   given "a browser open at the Google home page"
   when "the user searchs for 'Dogs'", {
       steps.search_for "Dogs"
   }
   and "the user clicks on the Wikipedia entry", {
       steps.click_on_search_result_with_title "Dog - Wikipedia, the free encyclopedia"
   }
   then "the Dogs entry in Wikipedia should appear on the first page", {
   }
}

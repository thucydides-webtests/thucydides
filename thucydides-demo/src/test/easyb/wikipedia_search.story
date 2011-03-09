using "thucydides"

scenario "Searching for a topic on Wikipedia",{
	given "the user opens the Wikipedia home page",{
        //steps.open_home_page()
    }
	when "the user searches for cats", {
        //steps.searchFor("cats");
    }
	then "the user should find the article on Cats", {
        //steps.resultListShouldContain("Cat - Wikipedia, the free encyclopedia");
    }
}
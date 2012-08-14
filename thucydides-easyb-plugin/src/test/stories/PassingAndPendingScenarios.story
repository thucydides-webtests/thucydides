using "thucydides"

scenario "Select entry in dropdown list", {
    given "we are on the Thucydides demo site", {}
    when "the user selects the 'Label 2' option", {}
    then "this option should be selected", {}
    and "this option should also be selected", {}
}

scenario "Select entry in dropdown list using steps", {
    given "we are on the Thucydides demo site again"
    when "the user fills in the form"
    then "the chosen options should be displayed"
}

package net.thucydides.jbehave.stories

import org.jbehave.core.annotations.Given

Scenario: Running a simple successful JBehave story

Given a JBehave story
When we run the story with Thucydides
Then it should generate a Thucydides report for this story
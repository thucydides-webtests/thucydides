package net.thucydides.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static net.thucydides.core.model.TestStepFactory.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenComparingUserStories {

    @Test
    public void user_stories_with_identical_fields_are_equal() {
        UserStory userStory1 = new UserStory("A User Story", "US1", "a.user.Story");
        UserStory userStory2 = new UserStory("A User Story", "US1", "a.user.Story");

        assertThat(userStory1, is(userStory2));
    }

    @Test
    public void user_stories_with_different_names_are_different() {
        UserStory userStory1 = new UserStory("A User Story", "US1", "a.user.Story");
        UserStory userStory2 = new UserStory("Another User Story", "US1", "a.user.Story");

        assertThat(userStory1, is(not(userStory2)));
    }

    @Test
    public void user_stories_with_different_codes_are_different() {
        UserStory userStory1 = new UserStory("A User Story", "US1", "a.user.Story");
        UserStory userStory2 = new UserStory("A User Story", "US2", "a.user.Story");

        assertThat(userStory1, is(not(userStory2)));
    }

    @Test
    public void user_stories_with_different_sources_are_different() {
        UserStory userStory1 = new UserStory("A User Story", "US1", "a.user.Story");
        UserStory userStory2 = new UserStory("A User Story", "US1", "another.user.Story");

        assertThat(userStory1, is(not(userStory2)));
    }

    @Test
    public void user_stories_can_be_compared_with_null() {
        UserStory userStory1 = new UserStory("A User Story", "US1", "a.user.Story");

        assertThat(userStory1.equals(null), is(false));
    }


}

package net.thucydides.core.model;

/**
 * A User Story or Use Case being tested by an acceptance test.
 * 
 * @author johnsmart
 */
public class UserStory {

    private final String name;
    private final String code;

    public UserStory(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

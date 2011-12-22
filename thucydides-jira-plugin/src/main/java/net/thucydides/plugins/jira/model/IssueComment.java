package net.thucydides.plugins.jira.model;

/**
 * A comment associated with a given issue.
 */
public class IssueComment {

    private final Long id;
    private final String text;
    private final String author;

    public IssueComment(Long id, String text, String author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
}

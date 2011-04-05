package net.thucydides.core.model;

import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import net.thucydides.core.util.EqualsUtils;

/**
 * A User Story or Use Case being tested by an acceptance test. 
 * A User Story is verified by a set of acceptance tests.
 * 
 * @author johnsmart
 */
public class UserStory {

    /**
     * A human-readable name for this user story.
     */
    private final String name;

    /**
     * An optional code identifying this story, such as a JIRA issue number.
     */
    private final String code;

    /**
     * Where the user story is defined, such as the JUnit class or the net.thucydides.easyb
     * story. This is used to uniquely group test results by user story.
     */
    private final String source;

    public UserStory(final String name, final String code, final String source) {
        this.name = name;
        this.code = code;
        this.source = source;
    }

    public String getReportName(final ReportNamer.ReportType type) {
        ReportNamer reportNamer = new ReportNamer(type);
        return reportNamer.getNormalizedTestNameFor(this);
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nullSafeHashCodeOf(code);
        result = prime * result + nullSafeHashCodeOf(name);
        result = prime * result + nullSafeHashCodeOf(source);
        return result;
    }

    private int nullSafeHashCodeOf(final String value) {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserStory that = (UserStory) obj;

        return EqualsUtils.areEqual(this.code, that.code)
                && EqualsUtils.areEqual(this.name, that.name)
                && EqualsUtils.areEqual(this.source, that.source);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

}

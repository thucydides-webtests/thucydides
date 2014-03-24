package net.thucydides.core.model;

import com.google.common.base.Preconditions;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class TestTag implements Comparable<TestTag> {

    public static final TestTag EMPTY_TAG = new TestTag("","");

    private final String name;
    private final String type;

    private TestTag(String name, String type) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static TestTagBuilder withName(final String tagName) {
        return new TestTagBuilder(tagName);
    }

    public static TestTag withValue(String value) {
        if (value.contains(":")) {
            int separatorPosition = value.indexOf(":");
            String type = value.substring(0, separatorPosition).trim();
            String name = value.substring(separatorPosition + 1).trim();
            return TestTag.withName(name).andType(type);
        } else {
            return TestTag.withName(value.trim()).andType("feature");
        }
    }

    @Override
    public int compareTo(TestTag otherTag) {
        int typeComparison = compare(getType(), otherTag.getType());
        if (typeComparison != 0) {
            return typeComparison;
        } else {
            return getName().compareToIgnoreCase(otherTag.getName());
        }
    }

    public static class TestTagBuilder {
        private final String name;

        public TestTagBuilder(String name) {
            this.name = name;
        }
        
        public TestTag andType(String type) {
            return new TestTag(name, type);
        } 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestTag testTag = (TestTag) o;

        if (!name.equals(testTag.name)) return false;
        if (!type.equals(testTag.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestTag{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

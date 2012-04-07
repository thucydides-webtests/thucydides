package net.thucydides.core.model;

import com.google.common.base.Preconditions;

public class TestTag {

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

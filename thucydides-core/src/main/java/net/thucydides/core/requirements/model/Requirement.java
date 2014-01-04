package net.thucydides.core.requirements.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.TestTag;

import java.util.Collections;
import java.util.List;

/**
 * A capability represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what capabilities have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Requirement implements Comparable {

    private String displayName;
    private String name;
    private String type;
    private String narrativeText;
    private String cardNumber;
    private List<Requirement> children;
    private List<Example> examples;
    private List<String> releaseVersions;

    public Requirement() {
        // Used by Jackson
    }

    protected Requirement(String name, String displayName, String cardNumber, String type, String narrativeText,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions) {
        this.name = name;
        this.displayName = displayName;
        this.cardNumber = cardNumber;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = ImmutableList.copyOf(children);
        this.examples = ImmutableList.copyOf(examples);
        this.releaseVersions = ImmutableList.copyOf(releaseVersions);
    }

    protected Requirement(String name, String displayName, String cardNumber, String type, String narrativeText) {
        this.name = name;
        this.displayName = displayName;
        this.cardNumber = cardNumber;
        this.type = type;
        this.narrativeText = narrativeText;
        this.children = Collections.EMPTY_LIST;
        this.examples = Collections.EMPTY_LIST;
        this.releaseVersions = Collections.EMPTY_LIST;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String childType() {
        return (!children.isEmpty()) ? children.get(0).getType() : null;
    }

    public String getNarrativeText() {
        return narrativeText;
    }

    public List<String> getReleaseVersions() {
        return releaseVersions;
    }

    @JsonIgnore
    public int getChildrenCount() {
        return children.size();
    }

    public List<Requirement> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public List<Example> getExamples() {
        return ImmutableList.copyOf(examples);
    }

    public Boolean hasExamples() {
        return !examples.isEmpty();
    }

    @JsonIgnore
    public int getExampleCount() {
        return examples.size();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int compareTo(Object otherRequirement) {
        return name.compareTo(((Requirement) otherRequirement).getName());
    }

    public static RequirementBuilderNameStep named(String name) {
        return new RequirementBuilderNameStep(name);
    }

    public Requirement withChildren(List<Requirement> children) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.type, this.narrativeText, children, examples, releaseVersions);
    }

    public Requirement withExample(Example example) {
        List<Example> updatedExamples = Lists.newArrayList(examples);
        updatedExamples.add(example);
        return new Requirement(this.name, this.displayName, this.cardNumber, this.type, this.narrativeText, children, updatedExamples, releaseVersions);
    }

    public Requirement withExamples(List<Example> examples) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.type, this.narrativeText, children, examples, releaseVersions);
    }

    public Requirement withReleaseVersions(List<String> releaseVersions) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.type, this.narrativeText, children, examples, releaseVersions);
    }

    public boolean hasChildren() {
        return (children != null) && (!children.isEmpty());
    }

    public List<Requirement> getNestedChildren() {
        List<Requirement> nestedChildren = Lists.newArrayList();
        for(Requirement child : children) {
            nestedChildren.add(child);
            nestedChildren.addAll(child.getNestedChildren());
        }
        return ImmutableList.copyOf(nestedChildren);
    }

    public TestTag asTag() {
        return TestTag.withName(getName()).andType(getType());
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requirement that = (Requirement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }

    public Requirement withChild(Requirement child) {
        List<Requirement> newChildren = Lists.newArrayList(children);
        newChildren.remove(child);
        newChildren.add(child);
        return new Requirement(name,displayName,cardNumber,type,narrativeText, newChildren, examples,releaseVersions);
    }
}

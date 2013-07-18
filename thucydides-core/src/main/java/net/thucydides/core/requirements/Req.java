package net.thucydides.core.requirements;

import com.google.common.base.Objects;
import net.thucydides.core.requirements.model.Requirement;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

public class Req {

    private String name;
    private String publicName;
    private String cardNumber;
    private String type;
    private String narrativeText;
    private List<Req> children = new ArrayList<Req>();
    private Requirement requirement;
    private int level;
    private boolean annotatedClass;
    
    public Req() {
        //for jackson
    }

    public Req(int level, String name, String publicName, String cardNumber, String type, String narrativeText, List<Req> children) {
        this.setName(name);
        this.setPublicName(publicName);
        this.setCardNumber(cardNumber);
        this.setType(type);
        this.setNarrativeText(narrativeText);
        this.setChildren(children);
        this.setLevel(level);
    }

    public Req(String name, String publicName, int level) {
        this.setName(name);
        this.setPublicName(publicName);
        this.setLevel(level);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNarrativeText(String narrativeText) {
        this.narrativeText = narrativeText;
    }

    public void setChildren(List<Req> children) {
        this.children = children;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setAnnotatedClass(boolean annotatedClass) {
        this.annotatedClass = annotatedClass;
    }

    public String getName() {
        return name;
    }

    public String getPublicName() {
        return publicName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getType() {
        return type;
    }

    public String getNarrativeText() {
        return narrativeText;
    }

    public List<Req> getChildren() {
        return children;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAnnotatedClass() {
        return annotatedClass;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(name, publicName, cardNumber, type, narrativeText, children, requirement, level, annotatedClass);
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Req){
            final Req other = (Req) obj;
            return Objects.equal(name, other.name)
                    && Objects.equal(publicName, other.publicName)
                    && Objects.equal(cardNumber, other.cardNumber)
                    && Objects.equal(type, other.type)
                    && Objects.equal(narrativeText, other.narrativeText)
                    && Objects.equal(requirement, other.requirement)
                    && level == other.level
                    && Objects.equal(annotatedClass, other.annotatedClass)
                    && Objects.equal(children, other.children);
        } else{
            return false;
        }
    }
}

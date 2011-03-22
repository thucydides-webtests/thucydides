package net.thucydides.core.webelements;

import org.openqa.selenium.WebElement;

public class Checkbox {

    private final WebElement checkboxField;
    
    public Checkbox(WebElement checkboxField) {
        this.checkboxField = checkboxField;
    }

    public void setChecked(boolean value) {
        if (checkboxField.isSelected()) {
            clickToUnselect(value);
        } else {
            clickToSelect(value);
        }
    }
    
    private void clickToSelect(boolean value) {
        if (value) {
            checkboxField.click();
        }
    }

    private void clickToUnselect(boolean value) {
        if (!value) {
            checkboxField.click();
        }
    }

    public boolean isChecked() {
        return checkboxField.isSelected();
    }

}

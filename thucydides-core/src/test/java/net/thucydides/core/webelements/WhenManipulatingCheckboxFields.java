package net.thucydides.core.webelements;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebElement;
import static org.mockito.Mockito.*;

public class WhenManipulatingCheckboxFields {

    @Mock
    WebElement checkboxField;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void setting_to_true_should_click_an_unselected_checkbox() {
        Checkbox checkbox = new Checkbox(checkboxField);
        when(checkboxField.isSelected()).thenReturn(false);
        
        checkbox.setChecked(true);        
        
        verify(checkboxField).click();
    }
    
    @Test
    public void setting_to_false_should_do_nothing_an_unselected_checkbox() {
        Checkbox checkbox = new Checkbox(checkboxField);
        when(checkboxField.isSelected()).thenReturn(false);
        
        checkbox.setChecked(false);        
        
        verify(checkboxField, never()).click();
    }

    @Test
    public void setting_to_true_should_do_nothing_to_an_unselected_checkbox() {
        Checkbox checkbox = new Checkbox(checkboxField);
        when(checkboxField.isSelected()).thenReturn(true);
        
        checkbox.setChecked(true);        
        
        verify(checkboxField, never()).click();
    }
        
    @Test
    public void setting_to_false_should_click_on_selected_checkbox() {
        Checkbox checkbox = new Checkbox(checkboxField);
        when(checkboxField.isSelected()).thenReturn(true);
        
        checkbox.setChecked(false);        
        
        verify(checkboxField).click();
    }

}

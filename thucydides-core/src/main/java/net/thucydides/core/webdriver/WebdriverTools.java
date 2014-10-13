/*
 * Copyright 2014 Wakaleo Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.thucydides.core.webdriver;

import java.util.Iterator;
import java.util.Set;
import org.openqa.selenium.WebDriver;

/**
 *
 * This solution can be tested on this page:
 * http://samples.msdn.microsoft.com/workshop/samples/author/dhtml/refs/onbeforeunload.htm
 * 
 */
public abstract class WebdriverTools {
    
    
    //Navigating to blank page for all windows
    public static WebDriver prepareDriverToQuit(WebDriver driver) {  
        try
        {            
            Set<String> openedWindows = driver.getWindowHandles();
            Iterator<String> openedWindowsIterator = openedWindows.iterator();

            while(openedWindowsIterator.hasNext())
            {
                String newWindow = openedWindowsIterator.next();  
                driver.switchTo().window(newWindow);     
                driver.get("about:blank");                // Try to open blank page     
                try{
                    driver.switchTo().alert().accept();   // Accept exit if pop-up with confirmation appeared          
                }catch(Exception ex){}               
            }          
        }catch(Exception ex){}    
        
        return driver;   
    }
    
    
    //Navigating to blank page only for current window
    public static WebDriver prepareDriverToClose(WebDriver driver) {  
        try
        {         
            driver.get("about:blank");                // Try to open blank page         
            try{
                driver.switchTo().alert().accept();   // Accept exit if pop-up with confirmation appeared         
            }
            catch(Exception ex){}    
        }
        catch(Exception ex){}
        
        return driver;
    }
}

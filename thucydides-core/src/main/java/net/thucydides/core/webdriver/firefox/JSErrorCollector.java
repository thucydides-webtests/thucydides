/*
 * Copyright 2013 Wakaleo Consulting.
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
package net.thucydides.core.webdriver.firefox;

import java.util.List;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author johann.wiedmeier
 */
public class JSErrorCollector {
    
    public static void assertJSError(WebDriver webdriver){
        final List<JavaScriptError> jsErrors = JavaScriptError.readErrors(webdriver);
        if(!jsErrors.isEmpty()){
            String errorString="";
            for(int i=0; i< jsErrors.size(); i++){
                errorString = jsErrors.get(i).getErrorMessage() + " @Line " +
                        jsErrors.get(i).getLineNumber() + " of " +
                        jsErrors.get(i).getSourceName();
            }
            throw new AssertionError(errorString);
        }
        
    }
}

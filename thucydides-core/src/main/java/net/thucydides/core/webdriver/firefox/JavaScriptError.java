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

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.Map;

/**
* Holds information about a JavaScript error that has occurred in the browser.
* This can be currently only used with the {@link FirefoxDriver} (see {@link #addExtension(FirefoxProfile)}.
* @author Marc Guillemot
* @version $Revision: $
*/
public class JavaScriptError {
    private final String errorMessage;
    private final String sourceName;
    private final int lineNumber;	

    JavaScriptError(final Map<String, ? extends Object> map) {
        errorMessage = (String) map.get("errorMessage");
        sourceName = (String) map.get("sourceName");
        lineNumber = ((Number) map.get("lineNumber")).intValue();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getSourceName() {
        return sourceName;
    }
}
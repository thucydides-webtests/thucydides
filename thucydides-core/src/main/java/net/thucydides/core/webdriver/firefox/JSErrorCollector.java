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

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.Lists;
import net.thucydides.core.util.StringConstants;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

/**
 *
 * @author johann.wiedmeier
 */
public class JSErrorCollector {

    private static final String COLLECT_JAVASCRIPT_ERRORS = "return window.JSErrorCollector_errors ? window.JSErrorCollector_errors.pump() : []";

    private final WebDriver webdriver;

    public JSErrorCollector(WebDriver webdriver) {
        this.webdriver = webdriver;
    }

    public void checkForJavascriptErrors(){
        final List<JavaScriptError> jsErrors = readErrors(webdriver);
        if(!jsErrors.isEmpty()){
            StringBuffer errorMessages = new StringBuffer();
            for(JavaScriptError jsError : jsErrors){
                errorMessages.append(errorMessageFrom(jsError)).append(StringConstants.NEWLINE);
            }
            throw new AssertionError(errorMessages.toString());
        }
    }

    private String errorMessageFrom(JavaScriptError jsError) {
        return jsError.getErrorMessage() + " @Line " +
               jsError.getLineNumber() + " of " +
               jsError.getSourceName();
    }

    private final List<JavaScriptError> NO_ERRORS = Lists.newArrayList();

    /**
     * Gets the collected JavaScript errors that have occurred since last call to this method.
     * @param driver the driver providing the possibility to retrieved JavaScript errors
     * @return the errors or an empty list if the driver doesn't provide access to the JavaScript errors
     */
    @SuppressWarnings("unchecked")
    private List<JavaScriptError> readErrors(final WebDriver driver) {
        if (JavascriptExecutor.class.isAssignableFrom(driver.getClass())) {
            final List<Object> errors = (List<Object>) ((JavascriptExecutor) driver).executeScript(COLLECT_JAVASCRIPT_ERRORS);
            return convert(errors,toJavascriptErrors());
        } else {
            return NO_ERRORS;
        }
    }

    private Converter<Object, JavaScriptError> toJavascriptErrors() {
        return new Converter<Object, JavaScriptError>() {

            @Override
            public JavaScriptError convert(Object rawError) {
                return new JavaScriptError((Map<String, Object>) rawError);
            }
        };
    }
} 
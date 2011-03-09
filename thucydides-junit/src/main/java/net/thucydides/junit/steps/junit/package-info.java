/**
 * Junit-integration for test steps.
 * Steps should be framework-independent. This package contains support for integration with 
 * JUnit, in particular the StepInterceptor class, which uses cglib to notify JUnit whenever
 * a step method is completed. JUnit can use this to treat steps as "mini-tests".
 */
package net.thucydides.junit.steps.junit;
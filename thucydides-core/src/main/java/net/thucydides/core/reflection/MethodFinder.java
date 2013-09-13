package net.thucydides.core.reflection;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MethodFinder {

    private final Class targetClass;

    private MethodFinder(Class targetClass) {
        this.targetClass = targetClass;
    }

    public static MethodFinder inClass(Class targetClass) {
        return new MethodFinder(targetClass);
    }


    public List<Method> getAllMethods() {
        Set<Method> allMethods = Sets.newHashSet();
        allMethods.addAll(Arrays.asList(targetClass.getDeclaredMethods()));
        allMethods.addAll(Arrays.asList(targetClass.getMethods()));
        return Lists.newArrayList(allMethods);
    }
}

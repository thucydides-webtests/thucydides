package net.thucydides.junit.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.pipeline.converters.TypeConverter;
import net.thucydides.junit.pipeline.converters.TypeConverters;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestPipeline {

    private final static Object[] NO_ARGS = new Object[0];

    private final FilePathParser filePathParser;

    List<TestPipeline> pipelines = Lists.newArrayList();

    public TestPipeline() {
        filePathParser = new FilePathParser(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    @Managed
    protected WebDriver webDriver;

    @ManagedPages
    protected Pages pages;

    protected int number = 0;

    @Test
    public void pipeline(){
        initializePipelines();
        for (Method stageMethod : getStagedMethods()) {
            for (TestPipeline pipeline: pipelines) {
                execute(stageMethod).inPipeline(pipeline);
            }
        }
    }

    protected List<Map<String,String>> loadDataFrom(String filename) throws IOException {
        String instantiatedFilename = filePathParser.getInstanciatedPath(filename);
        return CSVData.fromFile(instantiatedFilename).asMaps();
    }

    private void initializePipelines() {
        Iterable<Map> datasets = getDataSets();
        int pipelineNumber = 1;
        for(Map<String, Object> fieldValues : datasets) {
            TestPipeline pipeline = newPipeline(pipelineNumber++);
            setFieldsIn(pipeline).usingDataFrom(fieldValues);
            initializeStepLibrariesIn(pipeline);
            pipelines.add(pipeline);
        }
    }

    private void initializeStepLibrariesIn(TestPipeline pipeline) {
        Thucydides.injectScenarioStepsInto(pipeline);
    }

    private TestPipeline newPipeline(int pipelineNumber) {
        Class testPipelineClass = this.getClass();
        try {
            TestPipeline pipeline = (TestPipeline) testPipelineClass.newInstance();
            copyFieldValuesTo(pipeline);
            pipeline.number = pipelineNumber;
            return pipeline;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not initialize the test pipeline", e);
        }
    }

    private void copyFieldValuesTo(TestPipeline pipeline) throws IllegalAccessException {
        Set<Field> fields = Fields.of(pipeline.getClass()).nonStaticFields();
        for(Field field: fields) {
            field.setAccessible(true);
            Object sourceValue = field.get(this);
            field.set(pipeline, sourceValue);
        }
    }

    private Iterable<Map> getDataSets() {
        Method dataProvider = firstMethodAnnotatedWith(DataProvider.class);
        return getDatasetFrom(dataProvider);
    }

    private FieldSetterBuilder setFieldsIn(Object target) {
        return new FieldSetterBuilder(target);
    }

    public List<TypeConverter> getCustomTypeConverters() {
        return Collections.emptyList();
    }

    public int getNumber() {
        return number;
    }

    private class FieldSetterBuilder {
        private final Object target;

        private FieldSetterBuilder(Object target) {
            this.target = target;
        }

        public void usingDataFrom(Map<String, Object> values) {
            for(String key : values.keySet()) {
                setFieldIfPresent(key, values.get(key));
            }
        }

        private void setFieldIfPresent(String key, Object value) {
            try {
                Field field = target.getClass().getDeclaredField(key);
                Object valueToSet = convert(field, value);
                field.setAccessible(true);
                field.set(target, valueToSet);
            } catch (IllegalAccessException ignored) {
            } catch (NoSuchFieldException fieldNotFound) {
                System.out.println("No matching field found for " + key);
            }
        }

        private Object convert(Field field, Object fieldValue) {
            TypeConverter typeConverter = getTypeConverterFor(field.getType());
            return typeConverter.valueOf(fieldValue);
        }

    }

    private TypeConverter getTypeConverterFor(Class<?> type) {
        List<TypeConverter> allTypeConverters = ImmutableList.<TypeConverter>builder()
                                                             .addAll(TypeConverters.getDefaultTypeConverters())
                                                             .addAll(getCustomTypeConverters())
                                                             .build();
        for(TypeConverter typeConverter : allTypeConverters) {
            if (typeConverter.appliesTo(type)) {
                return typeConverter;
            }
        }
        throw new IllegalArgumentException("No applicable type converter found for " + type);
    }


    private Iterable<Map> getDatasetFrom(Method dataProvider) {
        try {
            return (Iterable<Map>) dataProvider.invoke(this, NO_ARGS);
        } catch (Exception e) {
            throw new RuntimeException("Could not load dataset",e);
        }
    }

    private Method firstMethodAnnotatedWith(Class<? extends Annotation> annotation) {
        List<Method> methods = Arrays.asList(getClass().getDeclaredMethods());
        for(Method method : methods) {
            if (method.getAnnotation(annotation) != null) {
                return method;
            }
        }
        throw new IllegalArgumentException("No method annotated with the @DataProvider annotation found.");
    }

    private PipelineStageExecutor execute(Method stageMethod) {
        return new PipelineStageExecutor(stageMethod);
    }

    private class PipelineStageExecutor {
        private final Method stageMethod;

        private PipelineStageExecutor(Method stageMethod) {
            this.stageMethod = stageMethod;
        }

        public void inPipeline(TestPipeline pipeline) {
            try {
                Method localStageMethod = getStageMethodFor(pipeline);
                localStageMethod.invoke(pipeline, NO_ARGS);
            } catch (Exception e) {
                throw new AssertionError("Failed to run stage method " + stageMethod.getName());
            }
        }

        private Method getStageMethodFor(TestPipeline pipeline) {
            List<Method> localMethods = Arrays.asList(pipeline.getClass().getDeclaredMethods());
            for(Method method : localMethods) {
                if (method.getName().equals(stageMethod.getName())) {
                    return method;
                }
            }
            throw new IllegalArgumentException("No matching method found for " + stageMethod);
        }
    }

    public List<Method> getStagedMethods() {
        List<Method> allMethods = Arrays.asList(getClass().getDeclaredMethods());
        return stageMethodsIn(allMethods);
    }

    private List<Method> stageMethodsIn(List<Method> allMethods) {
        List<Method> stageMethods = Lists.newArrayList();
        for(Method method : allMethods){
            if (method.getAnnotation(Stage.class) != null) {
                stageMethods.add(method);
            }
        }
        Collections.sort(stageMethods, byStepOrder());
        return stageMethods;
    }

    private Comparator<Method> byStepOrder() {
        return new Comparator<Method>() {
            public int compare(Method methodA, Method methodB) {
                return stageNumber(methodA) - stageNumber(methodB);
            }
        };
    }

    int stageNumber(Method method) {
        return  method.getAnnotation(Stage.class).value();
    }
}

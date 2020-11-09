package com.medvedskiy.core.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

public class BeanInjector implements ParameterResolver, BeforeAllCallback {

    AnnotationConfigApplicationContext context;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> typeWanted = parameterContext.getParameter().getType();

        //a way to see if wanted type is in @ContextConfiguration. We get @ContextConfiguration, create context of
        //its @Configuration classes, see if type IS in new context
        Object[] beansOfThatType = context.getBeansOfType(typeWanted).values().toArray(Object[]::new);

        return parameterContext.getParameter().isAnnotationPresent(Value.class) || beansOfThatType.length > 0;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Value value = parameterContext.getParameter().getAnnotation(Value.class);
        if (value != null) {
            return context.getEnvironment().resolvePlaceholders(value.value());
        }
        return context.getBean(parameterContext.getParameter().getType());
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        context = new AnnotationConfigApplicationContext(
                extensionContext.getTestClass().orElseThrow().getAnnotation(ContextConfiguration.class).classes()
        );
    }
}

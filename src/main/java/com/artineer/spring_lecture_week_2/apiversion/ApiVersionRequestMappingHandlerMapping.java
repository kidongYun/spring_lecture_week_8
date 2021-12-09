package com.artineer.spring_lecture_week_2.apiversion;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return createCondition(typeAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(methodAnnotation);
    }

    private RequestCondition<?> createCondition(ApiVersion apiVersion) {
        if(apiVersion == null) {
           return null;
        }

        return new ApiVersionRequestCondition(apiVersion.value(), Version.MAX_VERSION);
    }
}

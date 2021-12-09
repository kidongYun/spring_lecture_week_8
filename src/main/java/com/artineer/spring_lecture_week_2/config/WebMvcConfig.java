package com.artineer.spring_lecture_week_2.config;

import com.artineer.spring_lecture_week_2.apiversion.ApiVersionRequestMappingHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}

package com.rodrigo.sistemafacturas.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {
//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         WebMvcConfigurer.super.addResourceHandlers(registry);
//         String resourcesPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
//         registry.addResourceHandler("/uploads/**")
//                 .addResourceLocations(resourcesPath);
//     }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error_403").setViewName("error_403");
    }
}

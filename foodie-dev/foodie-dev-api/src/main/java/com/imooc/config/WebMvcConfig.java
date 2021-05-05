package com.imooc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 实现静态资源的映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // 映射所有资源
                .addResourceLocations("classpath:/META-INF/resources/") // 映射swagger2
                .addResourceLocations("file:E:\\LiangFan\\WorkSpace\\"); // 映射本地静态资源
    }
}

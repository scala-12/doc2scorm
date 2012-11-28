package com.ipoint.coursegenerator.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.spring.HandlerModule;
import com.gwtplatform.dispatch.server.spring.configuration.DefaultModule;
import com.ipoint.coursegenerator.shared.GenerateCourse;

@Configuration
@Import({ DefaultModule.class })
public class CourseGeneratorServerModule extends HandlerModule {

    @Override
    protected void configureHandlers() {
	bindHandler(GenerateCourse.class, GenerateCourseActionHandler.class);
    }

    @Bean
    public GenerateCourseActionHandler getGenerateCourseActionHandler() {
        return new GenerateCourseActionHandler();
    }

}

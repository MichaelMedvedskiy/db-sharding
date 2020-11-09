package com.medvedskiy.api.config;

import com.medvedskiy.core.config.CoreConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CoreConfig.class,
})
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class ApiConfig {
}

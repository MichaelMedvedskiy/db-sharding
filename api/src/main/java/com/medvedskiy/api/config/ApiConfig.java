package com.medvedskiy.api.config;

import com.medvedskiy.core.config.CoreConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Config for running Api
 */
@Configuration
@Import({
        CoreConfig.class,
})
@EnableAutoConfiguration
public class ApiConfig {
}

package com.medvedskiy.api.config;

import com.medvedskiy.repository.config.DatabaseFirst;
import com.medvedskiy.repository.config.DatabaseSecond;
import com.medvedskiy.repository.config.DatabaseThird;
import com.medvedskiy.repository.config.RepositoriesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        RepositoriesConfig.class,
        DatabaseFirst.class,
        DatabaseSecond.class,
        DatabaseThird.class,
})
//@ComponentScan("com.medvedskiy.api.controllers")
public class ApiConfig {
}

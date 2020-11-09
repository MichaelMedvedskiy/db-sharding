package com.medvedskiy.api.config;

import com.medvedskiy.core.config.CoreConfig;
import com.medvedskiy.repository.config.DatabaseFirst;
import com.medvedskiy.repository.config.DatabaseSecond;
import com.medvedskiy.repository.config.DatabaseThird;
import com.medvedskiy.repository.config.RepositoriesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CoreConfig.class,
})
public class ApiConfig {
}

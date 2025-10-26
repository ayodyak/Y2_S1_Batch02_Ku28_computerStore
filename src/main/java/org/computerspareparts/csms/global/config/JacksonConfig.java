package org.computerspareparts.csms.global.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        // Register the Hibernate module so Jackson can handle lazy-loaded proxies
        Hibernate6Module module = new Hibernate6Module();
        // By default the module will NOT force lazy loading; leave default to avoid unexpected DB hits
        return module;
    }
}


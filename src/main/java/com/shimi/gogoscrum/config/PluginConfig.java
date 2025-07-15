package com.shimi.gogoscrum.config;

import org.pf4j.spring.SingletonSpringExtensionFactory;
import org.pf4j.spring.SpringExtensionFactory;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginConfig {
    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager(){
            @Override
            protected SpringExtensionFactory createExtensionFactory() {
                return new SingletonSpringExtensionFactory(this);
            }
        };
    }
}

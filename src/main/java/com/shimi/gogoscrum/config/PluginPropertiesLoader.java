package com.shimi.gogoscrum.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class PluginPropertiesLoader {
    private static final Logger log = LoggerFactory.getLogger(PluginPropertiesLoader.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer loadPluginProperties(Environment environment) throws IOException {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        // base application.properties are already loaded, now add plugin folder
        Path pluginsDir = Paths.get("./plugins");
        List<Resource> resources = new ArrayList<>();

        if (Files.exists(pluginsDir)) {
            try (Stream<Path> paths = Files.list(pluginsDir)) {
                paths.filter(p -> p.toString().endsWith(".properties"))
                        .forEach(p -> resources.add(new FileSystemResource(p.toFile())));
            }

            if (log.isInfoEnabled()) {
                log.info("Loaded plugin properties from: {}", resources);
            }
        }

        configurer.setLocations(resources.toArray(new Resource[0]));
        configurer.setIgnoreResourceNotFound(true);
        return configurer;
    }
}

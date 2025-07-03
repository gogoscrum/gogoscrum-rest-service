package com.shimi.gogoscrum;

import com.shimi.gsf.config.GsfDefaultExceptionHandler;
import com.shimi.gsf.config.GsfDefaultWebMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import({GsfDefaultWebMvcConfig.class, GsfDefaultExceptionHandler.class})
@EnableScheduling
public class GogoscrumApplication {
    public static void main(String[] args) {
        SpringApplication.run(GogoscrumApplication.class, args);
    }
}

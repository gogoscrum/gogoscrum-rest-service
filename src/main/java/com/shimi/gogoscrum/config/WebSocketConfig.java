package com.shimi.gogoscrum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket configuration class to enable WebSocket support in the application.
 * The WebSocket connections will be used for real-time agile board updates and notifications.
 * This class registers the ServerEndpointExporter bean which is necessary for
 * handling WebSocket endpoints.
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

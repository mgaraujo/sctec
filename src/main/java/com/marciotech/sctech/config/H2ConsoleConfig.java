package com.marciotech.sctech.config;

import jakarta.servlet.Servlet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.h2.server.web.JakartaWebServlet")
@ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true")
public class H2ConsoleConfig {

    @Bean
    ServletRegistrationBean<Servlet> h2ConsoleServlet(
            @Value("${spring.h2.console.path:/h2-console}") String consolePath,
            @Value("${spring.h2.console.settings.web-allow-others:false}") boolean webAllowOthers) {

        String normalizedPath = (consolePath == null || consolePath.isBlank()) ? "/h2-console" : consolePath.trim();
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (normalizedPath.endsWith("/*")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 2);
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        Servlet servlet = newH2Servlet();

        ServletRegistrationBean<Servlet> registration =
                new ServletRegistrationBean<>(servlet, normalizedPath + "/*");
        registration.setName("H2Console");
        registration.setLoadOnStartup(1);

        if (webAllowOthers) {
            // Mirrors `spring.h2.console.settings.web-allow-others=true` behavior.
            registration.addInitParameter("-webAllowOthers", "true");
        }

        return registration;
    }

    private static Servlet newH2Servlet() {
        try {
            Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet");
            return (Servlet) servletClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create H2 JakartaWebServlet. Is H2 on the runtime classpath?", e);
        }
    }
}

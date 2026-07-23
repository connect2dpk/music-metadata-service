package com.deepak.music.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Stateless internationalization (i18n) configuration for REST APIs.
 * Locale is resolved from the HTTP Accept-Language header (example Accept-Language:de).
 */
@Configuration
public class I18nConfiguration {

    /**
     * Uses Accept-Language header based locale resolution with English default.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }
}



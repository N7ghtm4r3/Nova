package com.tecknobit.nova;

import com.tecknobit.apimanager.apis.ServerProtector;
import com.tecknobit.apimanager.exceptions.SaveData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.NoSuchAlgorithmException;

import static com.tecknobit.nova.Launcher.DEFAULT_CONFIGURATION_FILE_PATH;

@SpringBootApplication
@PropertySources({
        @PropertySource(value = "classpath:" + DEFAULT_CONFIGURATION_FILE_PATH),
        //@PropertySource(value = "file:" + CUSTOM_CONFIGURATION_FILE_PATH, ignoreResourceNotFound = true)
})
@EnableJpaRepositories("com.tecknobit.nova.helpers.repositories")
public class Launcher {

    /**
     * {@code DEFAULT_CONFIGURATION_FILE_PATH} the default path where find the default server configuration
     */
    public static final String DEFAULT_CONFIGURATION_FILE_PATH = "app.properties";

    public static final ServerProtector protector = new ServerProtector("tecknobit/nova/backend",
            " to correctly register a new user in the Nova system ");

    public static void main(String[] args) throws NoSuchAlgorithmException, SaveData {
        protector.launch(args);
        SpringApplication.run(Launcher.class, args);
    }

    /**
     * The {@code CORSAdvice} class is useful to set the CORS policy
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    @Configuration
    public static class CORSAdvice {

        /**
         * Method to set the CORS filter <br>
         * No any-params required
         */
        @Bean
        public FilterRegistrationBean corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(false);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", config);
            FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
            bean.setOrder(0);
            return bean;
        }

    }

    /**
     * The {@code ResourceConfigs} class is useful to set the configuration of the resources to correctly serve the
     * images by the server
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see WebMvcConfigurer
     *
    @Configuration
    public static class ResourcesConfigs implements WebMvcConfigurer {

        /**
         * Add handlers to serve static resources such as images, js, and, css
         * files from specific locations under web application root, the classpath,
         * and others.
         *
         * @see ResourceHandlerRegistry
         *
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + IMAGES_PATH)
                    .setCachePeriod(0)
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver());
        }

    }*/

}
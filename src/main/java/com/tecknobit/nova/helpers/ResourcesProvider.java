package com.tecknobit.nova.helpers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;

public class ResourcesProvider {

    /**
     * {@code DEFAULT_CONFIGURATION_FILE_PATH} the default path where find the default server configuration
     */
    public static final String DEFAULT_CONFIGURATION_FILE_PATH = "app.properties";

    public static final String CUSTOM_CONFIGURATION_FILE_PATH = "nova.properties";

    public static final String RESOURCES_PATH = "resources/";

    public static final String[] RESOURCES_DIRECTORIES = {"profiles", "logos", "assets", "reports"};

    public static final String PROFILES_DIRECTORY = RESOURCES_DIRECTORIES[0];

    public static final String LOGOS_DIRECTORY = RESOURCES_DIRECTORIES[1];

    public static final String ASSETS_DIRECTORY = RESOURCES_DIRECTORIES[2];

    public static final String REPORTS_DIRECTORY = RESOURCES_DIRECTORIES[3];

    public static final String RESOURCES_REPORTS_PATH = RESOURCES_PATH + REPORTS_DIRECTORY + "/";

    private ResourcesProvider() {
    }

    public static void createResourceDirectories() {
        createResourceDirectory(RESOURCES_PATH);
        for (String directory : RESOURCES_DIRECTORIES)
            createResourceDirectory(RESOURCES_PATH + directory);
    }

    private static void createResourceDirectory(String resDirectory) {
        File directory = new File(resDirectory);
        if(!directory.exists())
            if(!directory.mkdir())
                printError(resDirectory.replaceAll("/", ""));
    }

    private static void printError(String directory) {
        System.err.println("Error during the creation of the \"" + directory + "\" folder");
        System.exit(-1);
    }

    /**
     * The {@code ResourceConfigs} class is useful to set the configuration of the resources to correctly serve the
     * images by the server
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see WebMvcConfigurer
     */
    @Configuration
    public static class ResourcesConfigs implements WebMvcConfigurer {

        /**
         * Add handlers to serve static resources such as images, js, and, css
         * files from specific locations under web application root, the classpath,
         * and others.
         *
         * @see ResourceHandlerRegistry
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + RESOURCES_PATH)
                    .setCachePeriod(0)
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver());
        }

    }

}

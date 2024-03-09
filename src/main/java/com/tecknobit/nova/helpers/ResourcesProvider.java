package com.tecknobit.nova.helpers;

import java.io.File;

public class ResourcesProvider {

    /**
     * {@code DEFAULT_CONFIGURATION_FILE_PATH} the default path where find the default server configuration
     */
    public static final String DEFAULT_CONFIGURATION_FILE_PATH = "app.properties";

    public static final String CUSTOM_CONFIGURATION_FILE_PATH = "nova.properties";

    /**
     * {@code IMAGES_PATH} path for the images folder
     */
    public static final String IMAGES_PATH = "images/";

    public static final String[] IMAGES_DIRECTORIES = {"profiles", "logos"};

    public static final String PROFILES_DIRECTORY = IMAGES_DIRECTORIES[0];

    public static final String LOGOS_DIRECTORY = IMAGES_DIRECTORIES[1];

    private ResourcesProvider() {
    }

    public static void createResourceDirectories() {
        File images = new File(IMAGES_PATH);
        if(!images.exists())
            if(!images.mkdir())
                printError("images");
        for (String directory : IMAGES_DIRECTORIES) {
            File subDirectory = new File(IMAGES_PATH + directory);
            if(!subDirectory.exists())
                if(!subDirectory.mkdir())
                    printError(directory);
        }
    }

    private static void printError(String directory) {
        System.err.println("Error during the creation of the \"" + directory + "\" folder");
        System.exit(-1);
    }

}

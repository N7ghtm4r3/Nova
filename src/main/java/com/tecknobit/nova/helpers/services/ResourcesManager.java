package com.tecknobit.nova.helpers.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static com.tecknobit.nova.helpers.ResourcesProvider.RESOURCES_PATH;

public interface ResourcesManager {

    default String createResource(MultipartFile resource, String resourcesDirectory, String resourceId) {
        String suffix = Objects.requireNonNull(resource.getResource().getFilename()).split("\\.")[1];
        return resourcesDirectory + "/" + resourceId + "." + suffix;
    }

    default void saveResource(MultipartFile resource, String path) throws IOException {
        File resourceFile = new File(RESOURCES_PATH +  path);
        try (OutputStream outputStream = new FileOutputStream(resourceFile)) {
            outputStream.write(resource.getBytes());
        }
    }

}

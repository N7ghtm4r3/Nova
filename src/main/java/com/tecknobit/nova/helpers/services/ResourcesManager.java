package com.tecknobit.nova.helpers.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static com.tecknobit.nova.helpers.ResourcesProvider.IMAGES_PATH;

public interface ResourcesManager {

    default String createResource(MultipartFile resource, String resourcesDirectory, String resourceId) throws IOException {
        String suffix = Objects.requireNonNull(resource.getResource().getFilename()).split("\\.")[1];
        return resourcesDirectory + "/" + resourceId + "." + suffix;
    }

    default void saveResource(MultipartFile resource, String path) throws IOException {
        File file = new File(IMAGES_PATH + path);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(resource.getBytes());
        }
    }

}

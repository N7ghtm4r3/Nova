package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static com.tecknobit.nova.helpers.ResourcesProvider.*;

public interface ResourcesManager {

    @Wrapper
    default String createProfileResource(MultipartFile resource, String resourceId) {
        return createResource(resource, PROFILES_DIRECTORY, resourceId);
    }

    @Wrapper
    default String createLogoResource(MultipartFile resource, String resourceId) {
        return createResource(resource, LOGOS_DIRECTORY, resourceId);
    }

    @Wrapper
    default String createAssetResource(MultipartFile resource, String resourceId) {
        return createResource(resource, ASSETS_DIRECTORY, resourceId);
    }

    @Wrapper
    default String createReportResource(MultipartFile resource, String resourceId) {
        return createResource(resource, REPORTS_DIRECTORY, resourceId);
    }

    private String createResource(MultipartFile resource, String resourcesDirectory, String resourceId) {
        return resourcesDirectory + "/" + resourceId + getResourceExtension(resource);
    }

    private String getResourceExtension(MultipartFile resource) {
        String resourceName = Objects.requireNonNull(resource.getResource().getFilename());
        return resourceName.replace(resourceName.substring(0, resourceName.lastIndexOf(".")), "");
    }

    default void saveResource(MultipartFile resource, String path) throws IOException {
        File resourceFile = new File(RESOURCES_PATH +  path);
        try (OutputStream outputStream = new FileOutputStream(resourceFile)) {
            outputStream.write(resource.getBytes());
        }
    }

    @Wrapper
    default boolean deleteProfileResource(String profileId) {
        return deleteResource(PROFILES_DIRECTORY, profileId);
    }

    @Wrapper
    default boolean deleteLogoResource(String logoId) {
        return deleteResource(LOGOS_DIRECTORY, logoId);
    }

    @Wrapper
    default boolean deleteAssetResource(String assetId) {
        return deleteResource(ASSETS_DIRECTORY, assetId);
    }

    @Wrapper
    default boolean deleteReportResource(String reportId) {
        return deleteResource(REPORTS_DIRECTORY, reportId);
    }

    private boolean deleteResource(String resourcesDirectory, String resourceId) {
        File resourceFolder = new File(RESOURCES_PATH + resourcesDirectory);
        for (File resourceFile : Objects.requireNonNull(resourceFolder.listFiles()))
            if(resourceFile.getName().contains(resourceId))
                return resourceFile.delete();
        return false;
    }

}

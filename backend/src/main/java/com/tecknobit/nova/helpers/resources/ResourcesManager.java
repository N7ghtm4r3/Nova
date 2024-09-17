package com.tecknobit.nova.helpers.resources;

import com.tecknobit.apimanager.annotations.Wrapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * The {@code ResourcesManager} interface is useful to create and manage the resources files as profile pic,
 * project logo, asset and report
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public interface ResourcesManager {

    /**
     * Method to create the pathname for a profile pic
     *
     * @param resource: the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for a profile pic
     */
    @Wrapper
    default String createProfileResource(MultipartFile resource, String resourceId) {
        return createResource(resource, ResourcesProvider.PROFILES_DIRECTORY, resourceId);
    }

    /**
     * Method to create the pathname for a project logo
     *
     * @param resource: the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for a project logo
     */
    @Wrapper
    default String createLogoResource(MultipartFile resource, String resourceId) {
        return createResource(resource, ResourcesProvider.LOGOS_DIRECTORY, resourceId);
    }

    /**
     * Method to create the pathname for an asset
     *
     * @param resource: the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for an asset
     */
    @Wrapper
    default String createAssetResource(MultipartFile resource, String resourceId) {
        return createResource(resource, ResourcesProvider.ASSETS_DIRECTORY, resourceId);
    }

    /**
     * Method to create the pathname for a report
     *
     * @param resource: the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for a report
     */
    @Wrapper
    default String createReportResource(MultipartFile resource, String resourceId) {
        return createResource(resource, ResourcesProvider.REPORTS_DIRECTORY, resourceId);
    }

    /**
     * Method to create the pathname of a resource file
     *
     * @param resource: the resource from create its pathname
     * @param resourcesDirectory: the specific resources directory to store the resource file
     * @param resourceId: the resource identifier
     * @return the pathname created of the resource file
     */
    private String createResource(MultipartFile resource, String resourcesDirectory, String resourceId) {
        return resourcesDirectory + "/" + resourceId + getSuffixResource(resource);
    }

    /**
     * Method to get the suffix of a resource file
     *
     * @param resource: the resource file from get its suffix
     * @return the suffix of the resource file as {@link String}
     */
    private String getSuffixResource(MultipartFile resource) {
        String resourceName = Objects.requireNonNull(resource.getResource().getFilename());
        return resourceName.replace(resourceName.substring(0, resourceName.lastIndexOf(".")), "");
    }

    /**
     * Method to save a resource file
     *
     * @param resource: the resource to save
     * @param path: the path where save the resource file
     * @throws IOException when an error occurred during the resource file saving
     */
    default void saveResource(MultipartFile resource, String path) throws IOException {
        File resourceFile = new File(ResourcesProvider.RESOURCES_PATH +  path);
        try (OutputStream outputStream = new FileOutputStream(resourceFile)) {
            outputStream.write(resource.getBytes());
        }
    }

    /**
     * Method to delete a profile pic
     *
     * @param profileId: the profile pic identifier of the profile pic to delete
     * @return whether the profile pic has been deleted as boolean
     */
    @Wrapper
    default boolean deleteProfileResource(String profileId) {
        return deleteResource(ResourcesProvider.PROFILES_DIRECTORY, profileId);
    }

    /**
     * Method to delete a project logo
     *
     * @param logoId: the project logo identifier of the logo to delete
     * @return whether the project logo has been deleted as boolean
     */
    @Wrapper
    default boolean deleteLogoResource(String logoId) {
        return deleteResource(ResourcesProvider.LOGOS_DIRECTORY, logoId);
    }

    /**
     * Method to delete an asset
     *
     * @param assetId: the asset identifier of the asset to delete
     * @return whether the asset has been deleted as boolean
     */
    @Wrapper
    default boolean deleteAssetResource(String assetId) {
        return deleteResource(ResourcesProvider.ASSETS_DIRECTORY, assetId);
    }

    /**
     * Method to delete a report
     *
     * @param reportId: the report identifier of the report to delete
     * @return whether the report has been deleted as boolean
     */
    @Wrapper
    default boolean deleteReportResource(String reportId) {
        return deleteResource(ResourcesProvider.REPORTS_DIRECTORY, reportId);
    }

    /**
     * Method to delete a resource file
     *
     * @param resourcesDirectory: the resources directory where delete a resource file
     * @param resourceId: the resource identifier of the resource to delete
     * @return whether the resource has been deleted as boolean
     */
    private boolean deleteResource(String resourcesDirectory, String resourceId) {
        File resourceFolder = new File(ResourcesProvider.RESOURCES_PATH + resourcesDirectory);
        for (File resourceFile : Objects.requireNonNull(resourceFolder.listFiles()))
            if(resourceFile.getName().contains(resourceId))
                return resourceFile.delete();
        return false;
    }

}

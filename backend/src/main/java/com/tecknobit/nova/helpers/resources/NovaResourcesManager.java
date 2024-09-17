package com.tecknobit.nova.helpers.resources;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.equinox.resourcesutils.ResourcesManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * The {@code NovaResourcesManager} interface is useful to create and manage the resources files as profile pic,
 * project logo, asset and report
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ResourcesManager
 */
public interface NovaResourcesManager extends ResourcesManager {

    /**
     * {@code LOGOS_DIRECTORY} the logos directory where are stored the logos of the projects
     */
    String LOGOS_DIRECTORY = "logos";

    /**
     * {@code ASSETS_DIRECTORY} the assets directory where are stored the assets of the releases uploaded
     */
    String ASSETS_DIRECTORY = "assets";

    /**
     * {@code REPORTS_DIRECTORY} the reports directory where are stored the reports of the releases created
     */
    String REPORTS_DIRECTORY = "reports";

    /**
     * {@code RESOURCES_REPORTS_PATH} the complete reports path from resources directory
     */
    String RESOURCES_REPORTS_PATH = RESOURCES_PATH + REPORTS_DIRECTORY + "/";

    /**
     * Method to create the pathname for a project logo
     *
     * @param resource: the resource from create its pathname
     * @param resourceId: the resource identifier
     * @return the pathname created for a project logo
     */
    @Wrapper
    default String createLogoResource(MultipartFile resource, String resourceId) {
        return createResource(resource, LOGOS_DIRECTORY, resourceId);
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
        return createResource(resource, ASSETS_DIRECTORY, resourceId);
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
        return createResource(resource, REPORTS_DIRECTORY, resourceId);
    }

    /**
     * Method to delete a project logo
     *
     * @param logoId: the project logo identifier of the logo to delete
     * @return whether the project logo has been deleted as boolean
     */
    @Wrapper
    default boolean deleteLogoResource(String logoId) {
        return deleteResource(LOGOS_DIRECTORY, logoId);
    }

    /**
     * Method to delete an asset
     *
     * @param assetId: the asset identifier of the asset to delete
     * @return whether the asset has been deleted as boolean
     */
    @Wrapper
    default boolean deleteAssetResource(String assetId) {
        return deleteResource(ASSETS_DIRECTORY, assetId);
    }

    /**
     * Method to delete a report
     *
     * @param reportId: the report identifier of the report to delete
     * @return whether the report has been deleted as boolean
     */
    @Wrapper
    default boolean deleteReportResource(String reportId) {
        return deleteResource(REPORTS_DIRECTORY, reportId);
    }

}

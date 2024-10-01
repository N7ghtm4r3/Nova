package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.nova.helpers.resources.NovaResourcesManager;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.NotificationsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseEventsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseTagRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleasesRepository;
import com.tecknobit.novacore.records.NovaNotification;
import com.tecknobit.novacore.records.NovaUser;
import com.tecknobit.novacore.records.project.Project;
import com.tecknobit.novacore.records.release.Release;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent;
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent;
import com.tecknobit.novacore.records.release.events.RejectedTag;
import com.tecknobit.novacore.records.release.events.ReleaseEvent;
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.equinox.environment.controllers.EquinoxController.generateIdentifier;
import static com.tecknobit.nova.helpers.ReportsProvider.VERSION_REGEX;
import static com.tecknobit.novacore.records.release.Release.ReleaseStatus.*;

/**
 * The {@code ReleasesHelper} class is useful to manage all the release database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaResourcesManager
 */
@Service
public class ReleasesHelper implements NovaResourcesManager {

    /**
     * {@code releasesRepository} instance for the releases repository
     */
    @Autowired
    private ReleasesRepository releasesRepository;

    /**
     * {@code releaseEventsRepository} instance for the events of releases repository
     */
    @Autowired
    private ReleaseEventsRepository releaseEventsRepository;

    /**
     * {@code releaseTagRepository} instance for the tags of releases repository
     */
    @Autowired
    private ReleaseTagRepository releaseTagRepository;

    /**
     * {@code notificationsRepository} instance useful to manage the notifications
     */
    @Autowired
    private NotificationsRepository notificationsRepository;

    /**
     * Method to add a new release
     *
     * @param requesterUser: the user who made the request to add the release
     * @param project: the project where attach the release
     * @param releaseId: the identifier of the release
     * @param releaseVersion: the version of the release
     * @param releaseNotesContent: the notes attached to the release
     */
    public void addRelease(String requesterUser, Project project, String releaseId, String releaseVersion,
                           String releaseNotesContent) {
        releasesRepository.addRelease(
                releaseId,
                System.currentTimeMillis(),
                releaseVersion,
                project.getId(),
                releaseNotesContent
        );
        List<NovaUser> members = project.getProjectMembers();
        members.add(project.getAuthor());
        for(NovaUser member : members) {
            String memberId = member.getId();
            if(!memberId.equals(requesterUser)) {
                notificationsRepository.insertNotification(
                        generateIdentifier(),
                        project.getLogoUrl(),
                        releaseId,
                        releaseVersion,
                        ReleaseStatus.New.name(),
                        memberId
                );
            }
        }
    }

    /**
     * Method to edit an existing release
     *
     * @param releaseId: the identifier of the release
     * @param releaseVersion: the version of the release
     * @param releaseNotesContent: the notes attached to the release
     */
    public void editRelease(String releaseId, String releaseVersion, String releaseNotesContent) {
        if(releaseVersion == null) {
            releasesRepository.editRelease(
                    releaseId,
                    releaseNotesContent
            );
        } else {
            releasesRepository.editRelease(
                    releaseId,
                    releaseVersion,
                    releaseNotesContent
            );
        }
    }

    /**
     * Method to get a release
     *
     * @param releaseId: the release identifier
     * @return the release, if exists, as {@link Release}
     */
    public Release getRelease(String releaseId) {
        return releasesRepository.findById(releaseId).orElse(null);
    }

    /**
     * Method to read all the notifications related to a specific {@link Release} for a user
     *
     * @param userId: the identifier of the user
     * @param releaseId: the identifier of the release
     */
    public void readAllNotifications(String userId, String releaseId) {
        notificationsRepository.setUserNotificationsAsRed(userId, releaseId);
    }

    /**
     * Method to upload a new assets on a release
     *
     * @param requesterUser: the user who made the request to uploads the assets
     * @param project: the project where the release is attached
     * @param releaseId: the release identifier
     * @param assets: the assets to upload
     * @param comment: the comment for the uploaded assets
     *
     * @return whether the upload has been successful
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    public boolean uploadAssets(String requesterUser, Project project, String releaseId, MultipartFile[] assets,
                                String comment) throws IOException {
        String eventId = generateIdentifier();
        if(comment.isEmpty())
            comment = null;
        releaseEventsRepository.insertAssetUploading(
                eventId,
                System.currentTimeMillis(),
                releaseId,
                Verifying.name(),
                comment
        );
        for (MultipartFile asset : assets) {
            if(!asset.isEmpty()) {
                String assetId = generateIdentifier();
                String assetPath = createAssetResource(asset, assetId);
                releaseEventsRepository.insertAsset(
                        assetId,
                        assetPath,
                        eventId,
                        asset.getOriginalFilename()
                );
                saveResource(asset, assetPath);
            } else
                return false;
        }
        setVerifyingStatus(requesterUser, project, releaseId);
        return true;
    }

    /**
     * Method to approve a release assets
     *
     * @param requesterUser: the user who made the request to approve the assets
     * @param project: the project where the release is attached
     * @param releaseId: the release identifier
     * @param eventId: the event related to those assets
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    public void approveAssets(String requesterUser, Project project, String releaseId, String eventId) {
        setApprovedStatus(requesterUser, project, releaseId);
        releasesRepository.approveAsset(releaseId, System.currentTimeMillis());
        releaseEventsRepository.setUploadingCommented(eventId);
    }

    /**
     * Method reject a release assets
     *
     * @param requesterUser: the user who made the request to reject the assets
     * @param project: the project where the release is attached
     * @param releaseId: the release identifier
     * @param eventId: the event related to those assets
     * @param reasons: the reasons of the rejection
     * @param tags: the tags attached to the rejection
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    public void rejectAsset(String requesterUser, Project project, String releaseId, String eventId, String reasons,
                            ArrayList<ReleaseTag> tags) {
        setRejectedStatus(requesterUser, project, releaseId);
        String rejectedReleaseEventId = generateIdentifier();
        releaseEventsRepository.insertRejectedReleaseEvent(
                rejectedReleaseEventId,
                System.currentTimeMillis(),
                releaseId,
                Rejected.name(),
                reasons
        );
        for (ReleaseTag tag : tags)
            releaseTagRepository.insertRejectedTag(generateIdentifier(), tag.name(), rejectedReleaseEventId);
        releaseEventsRepository.setUploadingCommented(eventId);
    }

    /**
     * Method to insert a comment to a {@link RejectedTag}
     *
     * @param comment: the comment to add
     * @param rejectedTagId: the identifier of the tag where place the comment
     */
    public void insertTagComment(String comment, String rejectedTagId) {
        releaseTagRepository.fillRejectedTag(rejectedTagId, comment);
    }

    /**
     * Method to set the {@link ReleaseStatus#Verifying} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    private void setVerifyingStatus(String requesterUser, Project project, String releaseId) {
        setReleaseStatus(requesterUser, project, releaseId, Verifying);
    }

    /**
     * Method to set the {@link ReleaseStatus#Approved} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    private void setApprovedStatus(String requesterUser, Project project, String releaseId) {
        setReleaseStatus(requesterUser, project, releaseId, Approved);
    }

    /**
     * Method to set the {@link ReleaseStatus#Rejected} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    private void setRejectedStatus(String requesterUser, Project project, String releaseId) {
        setReleaseStatus(requesterUser, project, releaseId, Rejected);
    }

    /**
     * Method to set the {@link ReleaseStatus#Alpha} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    public void setAlphaStatus(String requesterUser, Project project, String releaseId) {
        setReleaseStatus(requesterUser, project, releaseId, Alpha);
    }

    /**
     * Method to set the {@link ReleaseStatus#Beta} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    public void setBetaStatus(String requesterUser, Project project, String releaseId) {
        setReleaseStatus(requesterUser, project, releaseId, Beta);
    }

    /**
     * Method to set the {@link ReleaseStatus#Latest} status
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     *
     * @apiNote will be created the related {@link NovaNotification} for each member, not the author of the request,
     * of the project
     */
    @Wrapper
    public void setLatestStatus(String requesterUser, Project project, String projectId, String releaseId) {
        releasesRepository.setAsFinished(projectId);
        setReleaseStatus(requesterUser, project, releaseId, Latest);
    }

    /**
     * Method to change the status of a release
     *
     * @param requesterUser: the user who made a request
     * @param project: the project where the release is attached
     * @param releaseId: the identifier of the release
     * @param status: the status to set
     */
    private void setReleaseStatus(String requesterUser, Project project, String releaseId, ReleaseStatus status) {
        releasesRepository.updateReleaseStatus(releaseId, status.name());
        Release release = getRelease(releaseId);
        if(status != Verifying && status != Rejected) {
            releaseEventsRepository.insertReleaseEvent(
                    generateIdentifier(),
                    System.currentTimeMillis(),
                    releaseId,
                    status.name()
            );
        }
        List<NovaUser> members = project.getProjectMembers();
        members.add(project.getAuthor());
        for(NovaUser member : members) {
            String memberId = member.getId();
            if(!memberId.equals(requesterUser)) {
                notificationsRepository.insertNotification(
                        generateIdentifier(),
                        project.getLogoUrl(),
                        releaseId,
                        release.getReleaseVersion(),
                        status.name(),
                        memberId
                );
            }
        }
    }

    /**
     * Method to delete a release and all the data related to it
     *
     * @param requesterUser: the user who made the request to delete the release
     * @param project: the project where the release is attached
     * @param release: the release to delete
     */
    public void deleteRelease(String requesterUser, Project project, Release release) {
        String releaseId = release.getId();
        for (ReleaseEvent event : release.getReleaseEvents()) {
            String eventId = event.getId();
            if(event instanceof AssetUploadingEvent) {
                for (AssetUploadingEvent.AssetUploaded asset : ((AssetUploadingEvent) event).getAssetsUploaded())
                    deleteAssetResource(asset.getId());
                releaseEventsRepository.deleteAssetUploadingReleaseEvent(eventId);
            } else if(event instanceof RejectedReleaseEvent)
                releaseEventsRepository.deleteRejectedReleaseEvent(eventId);
            else
                releaseEventsRepository.deleteReleaseEvent(eventId);
        }
        deleteReportResource(release.getReleaseVersion().replaceFirst(VERSION_REGEX, ""));
        releasesRepository.deleteRelease(releaseId);
        if(project != null) {
            List<NovaUser> members = project.getProjectMembers();
            members.add(project.getAuthor());
            for (NovaUser member : project.getProjectMembers()) {
                String memberId = member.getId();
                if(!memberId.equals(requesterUser)) {
                    notificationsRepository.insertReleaseDeletedNotification(
                            generateIdentifier(),
                            project.getLogoUrl(),
                            release.getReleaseVersion(),
                            memberId
                    );
                }
            }
        }
    }

}

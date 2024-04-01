package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.nova.helpers.resources.ResourcesManager;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.NotificationsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseEventsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseTagRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleasesRepository;
import com.tecknobit.novacore.records.User;
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

import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.novacore.records.release.Release.ReleaseStatus.*;

/**
 * The {@code ReleasesHelper} class is useful to manage all the release database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see ResourcesManager
 */
@Service
public class ReleasesHelper implements ResourcesManager {

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

    @Autowired
    private NotificationsRepository notificationsRepository;

    /**
     * Method to add a new release
     *
     * @param releaseId: the identifier of the release
     * @param releaseVersion: the version of the release
     * @param projectId: the identifier of the project where attach the release
     */
    public void addRelease(String projectId, String releaseId, String releaseVersion, String releaseNotesContent) {
        releasesRepository.addRelease(
                releaseId,
                System.currentTimeMillis(),
                releaseVersion,
                projectId,
                releaseNotesContent
        );
    }

    /**
     * Method to get a release
     * @param releaseId: the release identifier
     * @return the release, if exists, as {@link Release}
     */
    public Release getRelease(String releaseId) {
        return releasesRepository.findById(releaseId).orElse(null);
    }

    /**
     * Method to upload a new assets on a release
     *
     * @param releaseId: the release identifier
     * @param assets: the assets to upload
     *
     * @return whether the upload has been successful
     */
    public boolean uploadAssets(String releaseId, MultipartFile[] assets) throws IOException {
        String eventId = generateIdentifier();
        releaseEventsRepository.insertAssetUploading(
                eventId,
                System.currentTimeMillis(),
                releaseId,
                Verifying.name()
        );
        for (MultipartFile asset : assets) {
            if(!asset.isEmpty()) {
                String assetId = generateIdentifier();
                String assetPath = createAssetResource(asset, assetId);
                releaseEventsRepository.insertAsset(
                        assetId,
                        assetPath,
                        eventId
                );
                saveResource(asset, assetPath);
            } else
                return false;
        }
        setVerifyingStatus(releaseId);
        return true;
    }

    /**
     * Method to approve a release assets
     * @param releaseId: the release identifier
     * @param eventId: the event related to those assets
     */
    public void approveAsset(String releaseId, String eventId) {
        setApprovedStatus(releaseId);
        releasesRepository.approveAsset(releaseId, System.currentTimeMillis());
        releaseEventsRepository.setUploadingCommented(eventId);
    }

    /**
     * Method reject a release assets
     * @param releaseId: the release identifier
     * @param eventId: the event related to those assets
     * @param reasons: the reasons of the rejection
     * @param tags: the tags attached to the rejection
     */
    public void rejectAsset(String releaseId, String eventId, String reasons, ArrayList<ReleaseTag> tags) {
        setRejectedStatus(releaseId);
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
     * @param comment: the comment to add
     * @param rejectedTagId: the identifier of the tag where place the comment
     */
    public void insertTagComment(String comment, String rejectedTagId) {
        releaseTagRepository.fillRejectedTag(rejectedTagId, comment);
    }

    /**
     * Method to set the {@link ReleaseStatus#Verifying} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    private void setVerifyingStatus(String releaseId) {
        setReleaseStatus(releaseId, Verifying);
    }

    /**
     * Method to set the {@link ReleaseStatus#Approved} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    private void setApprovedStatus(String releaseId) {
        setReleaseStatus(releaseId, Approved);
    }

    /**
     * Method to set the {@link ReleaseStatus#Rejected} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    private void setRejectedStatus(String releaseId) {
        setReleaseStatus(releaseId, Rejected);
    }

    /**
     * Method to set the {@link ReleaseStatus#Alpha} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    public void setAlphaStatus(String releaseId) {
        setReleaseStatus(releaseId, Alpha);
    }

    /**
     * Method to set the {@link ReleaseStatus#Beta} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    public void setBetaStatus(String releaseId) {
        setReleaseStatus(releaseId, Beta);
    }

    /**
     * Method to set the {@link ReleaseStatus#Latest} status
     *
     * @param releaseId: the identifier of the release
     */
    @Wrapper
    public void setLatestStatus(String projectId, String releaseId) {
        releasesRepository.setAsFinished(projectId);
        setReleaseStatus(releaseId, Latest);
    }

    /**
     * Method to change the status of a release
     *
     * @param releaseId: the identifier of the release
     * @param status: the status to set
     */
    private void setReleaseStatus(String releaseId, ReleaseStatus status) {
        releasesRepository.updateReleaseStatus(releaseId, status.name());
        if(status != Verifying && status != Rejected) {
            releaseEventsRepository.insertReleaseEvent(
                    generateIdentifier(),
                    System.currentTimeMillis(),
                    releaseId,
                    status.name()
            );
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
        deleteReportResource(releaseId);
        releasesRepository.deleteRelease(releaseId);
        if(project != null) {
            String releaseVersion = release.getReleaseVersion();
            String authorId = project.getAuthor().getId();
            if(!authorId.equals(requesterUser))
                sendReleaseDeletedNotification(project, releaseVersion, authorId);
            for (User member : project.getProjectMembers())
                if(!member.getId().equals(requesterUser))
                    sendReleaseDeletedNotification(project, releaseVersion, member.getId());
        }
    }

    private void sendReleaseDeletedNotification(Project project, String releaseVersion, String memberId) {
        notificationsRepository.insertReleaseDeletedNotification(
                generateIdentifier(),
                project.getLogoUrl(),
                releaseVersion,
                memberId
        );
    }

}

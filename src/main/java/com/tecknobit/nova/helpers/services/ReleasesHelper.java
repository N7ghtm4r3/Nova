package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseEventsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseTagRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleasesRepository;
import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.Release.ReleaseStatus;
import com.tecknobit.nova.records.release.events.ReleaseEvent.ReleaseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.nova.records.release.Release.ReleaseStatus.*;

@Service
public class ReleasesHelper implements ResourcesManager {

    @Autowired
    private ReleasesRepository releasesRepository;

    @Autowired
    private ReleaseEventsRepository releaseEventsRepository;

    @Autowired
    private ReleaseTagRepository releaseTagRepository;

    public void addRelease(String projectId, String releaseId, String releaseVersion, String releaseNotesContent) {
        releasesRepository.addRelease(
                releaseId,
                System.currentTimeMillis(),
                releaseVersion,
                projectId,
                releaseNotesContent
        );
    }

    public Release getRelease(String releaseId) {
        return releasesRepository.findById(releaseId).orElse(null);
    }

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

    public void approveAsset(String releaseId, String eventId) {
        long eventDate = insertReleaseEvent(releaseId, Approved);
        setApprovedStatus(releaseId);
        releasesRepository.approveAsset(releaseId, eventDate);
        releaseEventsRepository.setUploadingCommented(eventId);
    }

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

    private long insertReleaseEvent(String releaseId, ReleaseStatus status) {
        long eventDate = System.currentTimeMillis();
        releaseEventsRepository.insertReleaseEvent(
                generateIdentifier(),
                eventDate,
                releaseId,
                status.name()
        );
        return eventDate;
    }

    @Wrapper
    private void setVerifyingStatus(String releaseId) {
        setReleaseStatus(releaseId, Verifying);
    }

    @Wrapper
    private void setApprovedStatus(String releaseId) {
        setReleaseStatus(releaseId, Approved);
    }

    @Wrapper
    private void setRejectedStatus(String releaseId) {
        setReleaseStatus(releaseId, Rejected);
    }

    @Wrapper
    private void setBetaStatus(String releaseId) {
        setReleaseStatus(releaseId, Beta);
    }

    private void setReleaseStatus(String releaseId, ReleaseStatus status) {
        releasesRepository.updateReleaseStatus(releaseId, status.name());
    }


}

package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleaseEventsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.ReleasesRepository;
import com.tecknobit.nova.records.release.Release;
import com.tecknobit.nova.records.release.Release.ReleaseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.tecknobit.nova.Launcher.generateIdentifier;
import static com.tecknobit.nova.helpers.ResourcesProvider.ASSETS_DIRECTORY;
import static com.tecknobit.nova.records.release.Release.ReleaseStatus.Beta;
import static com.tecknobit.nova.records.release.Release.ReleaseStatus.Verifying;

@Service
public class ReleasesHelper implements ResourcesManager {

    @Autowired
    private ReleasesRepository releasesRepository;

    @Autowired
    private ReleaseEventsRepository releaseEventsRepository;

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

    @Wrapper
    private void setVerifyingStatus(String releaseId) {
        setReleaseStatus(releaseId, Verifying);
    }

    @Wrapper
    private void setBetaStatus(String releaseId) {
        setReleaseStatus(releaseId, Beta);
    }

    private void setReleaseStatus(String releaseId, ReleaseStatus status) {
        releasesRepository.updateReleaseStatus(releaseId, status.name());
    }


}

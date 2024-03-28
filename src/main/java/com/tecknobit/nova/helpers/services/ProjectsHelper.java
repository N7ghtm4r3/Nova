package com.tecknobit.nova.helpers.services;

import com.tecknobit.nova.helpers.services.repositories.projectsutils.JoiningQRCodeRepository;
import com.tecknobit.nova.helpers.services.repositories.projectsutils.ProjectsRepository;
import com.tecknobit.nova.records.project.JoiningQRCode;
import com.tecknobit.nova.records.project.Project;
import com.tecknobit.nova.records.release.Release;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.nova.records.User.*;
import static com.tecknobit.nova.records.project.Project.AUTHOR_KEY;
import static com.tecknobit.nova.records.project.Project.LOGO_URL_KEY;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
public class ProjectsHelper implements ResourcesManager {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private JoiningQRCodeRepository joiningQRCodeRepository;

    @Autowired
    private ReleasesHelper releasesHelper;

    public HashMap<String, List<Project>> getProjects(String userId) {
        HashMap<String, List<Project>> projects = new HashMap<>();
        projects.put(AUTHORED_PROJECTS_KEY, projectsRepository.getAuthoredProjects(userId));
        projects.put(PROJECTS_KEY, projectsRepository.getProjects(userId));
        return projects;
    }

    public JSONObject addProject(String name, MultipartFile logo, String projectId, String authorId) throws IOException {
        String logoUrl = createLogoResource(logo, projectId);
        projectsRepository.addProject(
                projectId,
                logoUrl,
                name,
                authorId
        );
        saveResource(logo, logoUrl);
        return new JSONObject()
                .put(NAME_KEY, name)
                .put(LOGO_URL_KEY, logoUrl)
                .put(IDENTIFIER_KEY, projectId)
                .put(AUTHOR_KEY, authorId);
    }

    public Project getProject(String userId, String projectId) {
        return projectsRepository.getProject(projectId, userId);
    }

    public String createJoiningQrcode(String QRCodeId, String projectId, List<String> membersEmails, Role role,
                                    boolean createJoinQRCode) {
        String joinCode = null;
        if(createJoinQRCode)
            joinCode = randomAlphanumeric(6).toUpperCase();
        joiningQRCodeRepository.insertJoiningQRCode(QRCodeId, currentTimeMillis(), joinCode, projectId,
                formatAllowedEmails(membersEmails), role.name());
        return joinCode;
    }

    public JoiningQRCode getJoiningQrcode(String QRCodeId) {
        return joiningQRCodeRepository.getJoiningQRCode(QRCodeId);
    }

    public JoiningQRCode getJoiningQrcodeByJoinCode(String joinCode) {
        return joiningQRCodeRepository.getJoiningQRCodeByJoinCode(joinCode);
    }

    public void removeMemberFromMailingList(JoiningQRCode joiningQRCode, String email) {
        ArrayList<String> membersEmails = joiningQRCode.listEmails();
        if(membersEmails.contains(email)) {
            membersEmails.remove(email);
            String QRCodeId = joiningQRCode.getId();
            if(membersEmails.isEmpty())
                joiningQRCodeRepository.deleteJoiningQRCode(QRCodeId);
            else
                joiningQRCodeRepository.updateJoiningQRCode(QRCodeId, formatAllowedEmails(membersEmails));
        }
    }

    public void removeWrongEmailMember(JoiningQRCode joiningQRCode, String email) {
        removeMemberFromMailingList(joiningQRCode, email);
    }

    public void joinMember(JoiningQRCode joiningQRCode, String email, String memberId) {
        removeMemberFromMailingList(joiningQRCode, email);
        projectsRepository.joinMember(joiningQRCode.getProject().getId(), memberId);
    }

    private String formatAllowedEmails(List<String> emails) {
        return emails.toString().toLowerCase()
                .replace("[", "")
                .replace("]", "");
    }

    public void deleteJoiningQrcode(String QRCodeId) {
        joiningQRCodeRepository.deleteJoiningQRCode(QRCodeId);
    }

    public void removeMember(String projectId, String memberId) {
        projectsRepository.removeMember(projectId, memberId);
    }

    public void deleteProject(Project project) {
        String projectId = project.getId();
        for (Release release : project.getReleases())
            releasesHelper.deleteRelease(release);
        projectsRepository.removeAllMembers(projectId);
        projectsRepository.deleteProject(projectId);
        deleteLogoResource(projectId);
    }

    public record ProjectPayload(MultipartFile logoUrl, String name) {}

}

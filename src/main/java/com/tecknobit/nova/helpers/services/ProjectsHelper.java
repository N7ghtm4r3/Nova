package com.tecknobit.nova.helpers.services;

import com.tecknobit.nova.helpers.services.repositories.JoiningQRCodeRepository;
import com.tecknobit.nova.helpers.services.repositories.ProjectsRepository;
import com.tecknobit.nova.records.project.JoiningQRCode;
import com.tecknobit.nova.records.project.Project;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.nova.Launcher.BASE_64_ENCODER;
import static com.tecknobit.nova.helpers.ResourcesProvider.LOGOS_DIRECTORY;
import static com.tecknobit.nova.records.project.Project.AUTHOR_KEY;
import static com.tecknobit.nova.records.project.Project.LOGO_URL_KEY;
import static com.tecknobit.nova.records.User.*;
import static java.lang.System.currentTimeMillis;

@Service
public class ProjectsHelper implements ResourcesManager {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private JoiningQRCodeRepository joiningQRCodeRepository;

    public HashMap<String, List<Project>> getProjects(String userId) {
        HashMap<String, List<Project>> projects = new HashMap<>();
        projects.put(AUTHORED_PROJECTS_KEY, projectsRepository.getAuthoredProjects(userId));
        projects.put(PROJECTS_KEY, projectsRepository.getProjects(userId));
        return projects;
    }

    public JSONObject addProject(String name, MultipartFile logo, String projectId, String authorId) throws IOException {
        String logoUrl = createResource(logo, LOGOS_DIRECTORY, projectId);
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

    public void createJoiningQrcode(String QRCodeId, String projectId, List<String> membersEmails) {
        joiningQRCodeRepository.insertJoiningQRCode(QRCodeId, currentTimeMillis(), projectId, BASE_64_ENCODER
                .encodeToString(formatAllowedEmails(membersEmails).getBytes())
        );
    }

    public JoiningQRCode getJoiningQrcode(String QRCodeId) {
        return joiningQRCodeRepository.getJoiningQRCode(QRCodeId);
    }

    public void joinMember(JoiningQRCode joiningQRCode, String email, String memberId) {
        List<String> membersEmails = new ArrayList<>(joiningQRCode.listEmails());
        membersEmails.remove(email);
        joiningQRCodeRepository.updateJoiningQRCode(joiningQRCode.getId(), formatAllowedEmails(membersEmails));
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

    public record ProjectPayload(MultipartFile logoUrl, String name) {}

}

package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.nova.controllers.projectmanagers.ProjectsController;
import com.tecknobit.nova.helpers.resources.NovaResourcesManager;
import com.tecknobit.nova.helpers.services.repositories.projectsutils.JoiningQRCodeRepository;
import com.tecknobit.nova.helpers.services.repositories.projectsutils.ProjectsRepository;
import com.tecknobit.nova.helpers.services.repositories.releaseutils.NotificationsRepository;
import com.tecknobit.novacore.records.NovaUser;
import com.tecknobit.novacore.records.project.JoiningQRCode;
import com.tecknobit.novacore.records.project.Project;
import com.tecknobit.novacore.records.release.Release;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.equinox.environment.controllers.EquinoxController.generateIdentifier;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.NovaUser.*;
import static com.tecknobit.novacore.records.project.JoiningQRCode.JOINING_QRCODES_MEMBERS_KEY;
import static com.tecknobit.novacore.records.project.Project.*;
import static java.lang.System.currentTimeMillis;

/**
 * The {@code ProjectsHelper} class is useful to manage all the project database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NovaResourcesManager
 */
@Service
public class ProjectsHelper extends EquinoxItemsHelper<Project> implements NovaResourcesManager {

    /**
     * {@code JOIN_MEMBERS_QUERY} the query used to join members in a project
     */
    protected static final String JOIN_MEMBERS_QUERY =
            "REPLACE INTO " + PROJECT_MEMBERS_TABLE +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    MEMBER_IDENTIFIER_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code REMOVE_MEMBERS_QUERY} the query used to remove members from a project
     */
    private static final String REMOVE_MEMBERS_QUERY =
            "DELETE FROM " + PROJECT_MEMBERS_TABLE + " WHERE "
                    + IDENTIFIER_KEY + "='%s' " + "AND " + MEMBER_IDENTIFIER_KEY + " IN (";

    /**
     * {@code INSERT_JOINING_QR_CODE_MEMBERS} the query used to insert the members invited in a project with a {@link JoiningQRCode}
     */
    protected static final String INSERT_JOINING_QR_CODE_MEMBERS =
            "INSERT INTO " + JOINING_QRCODES_MEMBERS_KEY +
                    "(" +
                    IDENTIFIER_KEY + "," +
                    ROLE_KEY + "," +
                    EMAIL_KEY +
                    ")" +
                    " VALUES ";

    /**
     * {@code projectsRepository} instance for the projects repository
     */
    @Autowired
    private ProjectsRepository projectsRepository;

    /**
     * {@code joiningQRCodeRepository} instance for the joining qrcodes repository
     */
    @Autowired
    private JoiningQRCodeRepository joiningQRCodeRepository;

    /**
     * {@code releasesHelper} helper to manage the releases database operations
     */
    @Autowired
    private ReleasesHelper releasesHelper;

    /**
     * {@code notificationsRepository} instance useful to manage the notifications
     */
    @Autowired
    private NotificationsRepository notificationsRepository;

    /**
     * Method to get the project of a {@link NovaUser}
     * 
     * @param userId: the user identifier
     * @return the projects list, also where the user is the author, as {@link HashMap} of {@link String} and {@link List}
     * of {@link Project}
     */
    public HashMap<String, List<Project>> getProjects(String userId) {
        HashMap<String, List<Project>> projects = new HashMap<>();
        projects.put(PROJECTS_KEY, projectsRepository.getProjects(userId));
        return projects;
    }

    /**
     * Method to add a new project
     * 
     * @param name: the name of the project
     * @param logo: the logo of the project
     * @param projectId: the project identifier
     * @param authorId: the author identifier
     * @param members: the members of the project
     * @return the details of the new project created as {@link JSONObject}
     */
    public JSONObject addProject(String name, MultipartFile logo, ArrayList<String> members, String projectId,
                                 String authorId) throws IOException {
        String logoUrl = createLogoResource(logo, projectId);
        projectsRepository.addProject(
                projectId,
                logoUrl,
                name,
                authorId
        );
        members.add(authorId);
        executeInsertBatch(JOIN_MEMBERS_QUERY, RELATIONSHIP_VALUES_SLICE, members, query -> {
            int index = 1;
            for (String member : members) {
                query.setParameter(index++, projectId);
                query.setParameter(index++, member);
            }
        });
        saveResource(logo, logoUrl);
        return new JSONObject()
                .put(NAME_KEY, name)
                .put(LOGO_URL_KEY, logoUrl)
                .put(IDENTIFIER_KEY, projectId)
                .put(AUTHOR_KEY, authorId);
    }

    /**
     * Method to edit an existing project
     *
     * @param name: the name of the project
     * @param logo: the logo of the project
     * @param project: the project to edit
     * @param members: the members of the project
     */
    public void editProject(String name, MultipartFile logo, List<String> members, Project project) throws IOException {
        String projectId = project.getId();
        boolean logoEdited = logo != null && !logo.isEmpty();
        String logoUrl = null;
        if(logoEdited) {
            logoUrl = createLogoResource(logo, projectId + System.currentTimeMillis());
            projectsRepository.editProject(
                    projectId,
                    logoUrl,
                    name
            );
        } else {
            projectsRepository.editProject(
                    projectId,
                    name
            );
        }
        manageProjectMembers(project, members);
        if(logoEdited) {
            deleteLogoResource(projectId);
            saveResource(logo, logoUrl);
        }
    }

    private void manageProjectMembers(Project project, List<String> editedMembers) {
        String projectId = project.getId();
        manageItems(new ItemsManagementWorkflow() {
            @Override
            public List<String> getIds() {
                ArrayList<String> members = new ArrayList<>();
                for (NovaUser member : project.getProjectMembers())
                    members.add(member.getId());
                return members;
            }

            @Override
            public String insertQuery() {
                return JOIN_MEMBERS_QUERY;
            }

            @Override
            public String deleteQuery() {
                return REMOVE_MEMBERS_QUERY;
            }
        }, RELATIONSHIP_VALUES_SLICE, projectId, editedMembers, query -> {
            int index = 1;
            for (String member : editedMembers) {
                query.setParameter(index++, projectId);
                query.setParameter(index++, member);
            }
        });
    }

    /**
     * Method to get a project
     * 
     * @param userId: the user identifier
     * @param projectId: the project identifier
     * @return the project, if exists and the user is authorized, as {@link Project}
     */
    public Project getProject(String userId, String projectId) {
        return projectsRepository.getProject(projectId, userId);
    }

    /**
     * Method to create a new joining qrcode
     * 
     * @param QRCodeId: the identifier of the qrcode
     * @param projectId: the project identifier
     * @param invitedMembers: the mailing list of the members to add with their role
     * @return the textual join code, if created, as {@link String}
     */
    public String createJoiningQrcode(String QRCodeId, String projectId, List<JSONObject> invitedMembers) {
        String joinCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        joiningQRCodeRepository.insertJoiningQRCode(QRCodeId, currentTimeMillis(), joinCode, projectId);
        executeInsertBatch(INSERT_JOINING_QR_CODE_MEMBERS, TUPLE_VALUES_SLICE, invitedMembers, query -> {
            int index = 1;
            for (JSONObject member : invitedMembers) {
                query.setParameter(index++, QRCodeId);
                query.setParameter(index++, member.getString(ROLE_KEY));
                query.setParameter(index++, member.getString(EMAIL_KEY));
            }
        });
        return joinCode;
    }

    /**
     * Method to get an existing joining qrcode by its identifier
     * @param QRCodeId: the identifier of the joining qrcode
     * @return the joining qrcode as {@link JoiningQRCode}
     */
    public JoiningQRCode getJoiningQrcode(String QRCodeId) {
        return joiningQRCodeRepository.getJoiningQRCode(QRCodeId);
    }

    /**
     * Method to get an existing joining qrcode by its textual join code
     * @param joinCode: the textual join code
     * @return the joining qrcode as {@link JoiningQRCode}
     */
    public JoiningQRCode getJoiningQrcodeByJoinCode(String joinCode) {
        return joiningQRCodeRepository.getJoiningQRCodeByJoinCode(joinCode);
    }

    /**
     * Method to join a new member in a project
     *
     * @param joiningQRCode: the joining qrcode used to join
     * @param email: the email of the member to join
     * @param memberId: the identifier of the member
     */
    public void joinMember(JoiningQRCode joiningQRCode, String email, String memberId) {
        removeMemberFromMailingList(joiningQRCode, email);
        projectsRepository.joinMember(joiningQRCode.getProject().getId(), memberId);
    }

    /**
     * Method to remove a member from the mailing list of the joining qrcode. <br>
     * This method is used when a user joined in the project
     * @param joiningQRCode: the joining qrcode to update
     * @param email: the email of the member to remove
     */
    public void removeMemberFromMailingList(JoiningQRCode joiningQRCode, String email) {
        joiningQRCodeRepository.removeMemberFromMailingList(joiningQRCode.getId(), email);
    }

    /**
     * Method to delete a joining qrcode
     *
     * @param QRCodeId: the identifier of the qrcode to delete
     */
    public void deleteJoiningQrcode(String QRCodeId) {
        joiningQRCodeRepository.deleteJoiningQRCode(QRCodeId);
    }

    /**
     * Method to mark a member as {@link Role#Tester} of the project
     *
     * @param projectId: the project identifier
     * @param memberId: the member identifier to mark as tester
     */
    public void markMemberAsTester(String projectId, String memberId) {
        projectsRepository.markMemberAsTester(projectId, memberId);
    }

    /**
     * Method to remove a member from a project
     * @param projectId: the project identifier
     * @param memberId: the member identifier of the member to remove
     */
    public void removeMember(String projectId, String memberId) {
        projectsRepository.removeMember(projectId, memberId);
        projectsRepository.removeTester(projectId, memberId);
    }

    /**
     * Method to delete a project and all the data related to it
     *
     * @param authorId: the author identifier who made the request to delete the project
     * @param project: the project to delete
     */
    public void deleteProject(String authorId, Project project) {
        String projectId = project.getId();
        for (Release release : project.getReleases())
            releasesHelper.deleteRelease(null, null, release);
        projectsRepository.removeAllMembers(projectId);
        projectsRepository.removeAllTesters(projectId);
        List<NovaUser> members = project.getProjectMembers();
        members.add(project.getAuthor());
        for(NovaUser member : members) {
            String memberId = member.getId();
            if(!memberId.equals(authorId)) {
                notificationsRepository.insertProjectDeletedNotification(
                        generateIdentifier(),
                        project.getLogoUrl(),
                        memberId
                );
            }
        }
        projectsRepository.deleteProject(projectId);
        deleteLogoResource(projectId);
    }

    /**
     * Record class used ad payload for the {@link ProjectsController#addProject(String, String, ProjectPayload)} request
     *
     * @param logo_url: the logo of the project
     * @param name: the title of the project
     * @param projectMembers: the members of the project
     */
    public record ProjectPayload(MultipartFile logo_url, String name, String projectMembers) {

        /**
         * Method to get the members list from the payload <br>
         *
         * No-any params required
         *
         * @return the members list as {@link ArrayList} of {@link String}
         */
        @Returner
        public ArrayList<String> membersList() {
            ArrayList<String> members = new ArrayList<>();
            if(projectMembers != null) {
                JSONArray jMembers = new JSONArray(projectMembers);
                for (int j = 0; j < jMembers.length(); j++)
                    members.add(jMembers.getString(j));
            }
            return members;
        }

    }

}

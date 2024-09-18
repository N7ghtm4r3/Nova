package com.tecknobit.nova.helpers.services.repositories.projectsutils;

import com.tecknobit.novacore.records.project.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxUser.NAME_KEY;
import static com.tecknobit.novacore.records.NovaUser.MEMBER_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY;
import static com.tecknobit.novacore.records.project.Project.*;

/**
 * The {@code ProjectsRepository} interface is useful to manage the queries for the projects
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see Project
 */
@Service
@Repository
public interface ProjectsRepository extends JpaRepository<Project, String> {

    /**
     *  Method to execute the query to get the list of {@link Project} where the user who made the request is a member
     *
     * @param userId: the identifier of the user
     *
     * @return list of projects as {@link List} of {@link Project}
     */
    @Query(
            value = "SELECT " + PROJECTS_KEY + ".* FROM " + PROJECTS_KEY + " AS " + PROJECTS_KEY + " INNER JOIN "
                    + PROJECT_MEMBERS_TABLE + " AS " + PROJECT_MEMBERS_TABLE + " ON " + PROJECTS_KEY + "."
                    + IDENTIFIER_KEY + "=" + PROJECT_MEMBERS_TABLE + "." + IDENTIFIER_KEY + " WHERE "
                    + MEMBER_IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<Project> getProjects(
            @Param(IDENTIFIER_KEY) String userId
    );

    /**
     *  Method to execute the query to add a new {@link Project}
     *
     * @param projectId: the identifier of the project
     * @param logoUrl: the logo of the project formatted as url
     * @param name: the project name
     * @param author: the identifier of the author who create the project
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + PROJECTS_KEY +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + LOGO_URL_KEY + ","
                    + NAME_KEY + ","
                    + AUTHOR_KEY + " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + LOGO_URL_KEY + ","
                    + ":" + NAME_KEY + ","
                    + ":" + AUTHOR_KEY + ")",
            nativeQuery = true
    )
    void addProject(
            @Param(IDENTIFIER_KEY) String projectId,
            @Param(LOGO_URL_KEY) String logoUrl,
            @Param(NAME_KEY) String name,
            @Param(AUTHOR_KEY) String author
    );

    /**
     *  Method to execute the query to get an existing {@link Project} if the user is authorized
     *
     * @param projectId: the identifier of the project
     * @param userId: the identifier of the user
     *
     * @return the selected project as {@link Project}
     */
    @Query(
            value = "SELECT * FROM " + PROJECTS_KEY + " WHERE " + AUTHOR_KEY + "=:" + AUTHOR_KEY
                    + " AND " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY
                    + " UNION SELECT " + PROJECTS_KEY + ".* FROM " + PROJECTS_KEY + " AS " + PROJECTS_KEY
                    + " INNER JOIN " + PROJECT_MEMBERS_TABLE + " AS " + PROJECT_MEMBERS_TABLE
                    + " ON " + PROJECTS_KEY + "." + IDENTIFIER_KEY + "=" + PROJECT_MEMBERS_TABLE
                    + "." + IDENTIFIER_KEY + " WHERE " + PROJECT_MEMBERS_TABLE + "." + MEMBER_IDENTIFIER_KEY
                    + "=:" + AUTHOR_KEY + " AND " + PROJECTS_KEY + "." + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    Project getProject(
          @Param(IDENTIFIER_KEY) String projectId,
          @Param(AUTHOR_KEY) String userId
    );

    /**
     *  Method to execute the query to join a new member in an existing {@link Project}
     *
     * @param projectId: the identifier of the project
     * @param memberId: the identifier of the member to join
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + PROJECT_MEMBERS_TABLE +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + MEMBER_IDENTIFIER_KEY +
                    " )"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + MEMBER_IDENTIFIER_KEY
                    + ")",
            nativeQuery = true
    )
    void joinMember(
            @Param(IDENTIFIER_KEY) String projectId,
            @Param(MEMBER_IDENTIFIER_KEY) String memberId
    );

    /**
     *  Method to execute the query to remove a member from an existing {@link Project}
     *
     * @param projectId: the identifier of the project
     * @param memberId: the identifier of the member to remove
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + PROJECT_MEMBERS_TABLE + " WHERE " + IDENTIFIER_KEY + "=:"
                    + IDENTIFIER_KEY + " AND " + MEMBER_IDENTIFIER_KEY + "=:" + MEMBER_IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeMember(
            @Param(IDENTIFIER_KEY) String projectId,
            @Param(MEMBER_IDENTIFIER_KEY) String memberId
    );

    /**
     *  Method to execute the query to remove all the members from an existing {@link Project}
     *
     * @param projectId: the identifier of the project
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + PROJECT_MEMBERS_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void removeAllMembers(
            @Param(IDENTIFIER_KEY) String projectId
    );

    /**
     *  Method to execute the query to delete an existing {@link Project}
     *
     * @param projectId: the identifier of the project to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + PROJECTS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteProject(
            @Param(IDENTIFIER_KEY) String projectId
    );

}

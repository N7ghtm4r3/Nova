package com.tecknobit.nova.helpers.services.repositories;

import com.tecknobit.nova.records.project.JoiningQRCode;
import com.tecknobit.nova.records.project.Project;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.User.*;
import static com.tecknobit.nova.records.project.JoiningQRCode.JOINING_QRCODES_TABLE;
import static com.tecknobit.nova.records.project.Project.*;
import static com.tecknobit.nova.records.release.Release.CREATION_DATE_KEY;

@Service
@Repository
public interface ProjectsRepository extends JpaRepository<Project, String> {

    @Query(
            value = "SELECT * FROM " + PROJECTS_KEY + " WHERE " + AUTHOR_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<Project> getAuthoredProjects(
            @Param(IDENTIFIER_KEY) String userId
    );

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

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + JOINING_QRCODES_TABLE +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + CREATION_DATE_KEY + ","
                    + PROJECT_IDENTIFIER_KEY + ","
                    + PROJECT_MEMBERS_TABLE + ")"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + CREATION_DATE_KEY + ","
                    + ":" + PROJECT_IDENTIFIER_KEY + ","
                    + ":" + PROJECT_MEMBERS_TABLE
                    + ")",
            nativeQuery = true
    )
    void insertJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(PROJECT_IDENTIFIER_KEY) String projectId,
            @Param(PROJECT_MEMBERS_TABLE) String projectMembers
    );

    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

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

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

}

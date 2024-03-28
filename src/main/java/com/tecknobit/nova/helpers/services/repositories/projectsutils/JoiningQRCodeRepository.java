package com.tecknobit.nova.helpers.services.repositories.projectsutils;

import com.tecknobit.novacore.records.project.JoiningQRCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.ROLE_KEY;
import static com.tecknobit.novacore.records.project.JoiningQRCode.JOINING_QRCODES_TABLE;
import static com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_TABLE;
import static com.tecknobit.novacore.records.release.Release.CREATION_DATE_KEY;

@Service
@Repository
public interface JoiningQRCodeRepository extends JpaRepository<JoiningQRCode, String> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + JOINING_QRCODES_TABLE +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + CREATION_DATE_KEY + ","
                    + JOIN_CODE_KEY + ","
                    + PROJECT_IDENTIFIER_KEY + ","
                    + PROJECT_MEMBERS_TABLE + ","
                    + ROLE_KEY + ")"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + CREATION_DATE_KEY + ","
                    + ":" + JOIN_CODE_KEY + ","
                    + ":" + PROJECT_IDENTIFIER_KEY + ","
                    + ":" + ROLE_KEY + ","
                    + ":" + PROJECT_MEMBERS_TABLE
                    + ")",
            nativeQuery = true
    )
    void insertJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(JOIN_CODE_KEY) String joinCode,
            @Param(PROJECT_IDENTIFIER_KEY) String projectId,
            @Param(ROLE_KEY) String role,
            @Param(PROJECT_MEMBERS_TABLE) String projectMembers
    );

    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + JOIN_CODE_KEY + "=:" + JOIN_CODE_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCodeByJoinCode(
            @Param(JOIN_CODE_KEY) String joinCode
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + JOINING_QRCODES_TABLE + " SET " + PROJECT_MEMBERS_TABLE + "=:" + PROJECT_MEMBERS_TABLE
                + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void updateJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(PROJECT_MEMBERS_TABLE) String projectMembers
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

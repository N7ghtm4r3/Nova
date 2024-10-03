package com.tecknobit.nova.helpers.services.repositories.projectsutils;

import com.tecknobit.novacore.records.project.JoiningQRCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.EMAIL_KEY;
import static com.tecknobit.novacore.records.project.JoiningQRCode.*;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_TABLE;
import static com.tecknobit.novacore.records.release.Release.CREATION_DATE_KEY;

/**
 * The {@code JoiningQRCodeRepository} interface is useful to manage the queries for the joining qrcodes
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see JoiningQRCode
 */
@Service
@Repository
public interface JoiningQRCodeRepository extends JpaRepository<JoiningQRCode, String> {

    /**
     *  Method to execute the query to add a new {@link JoiningQRCode}
     *
     * @param joiningQRCodeId: the identifier of the qrcode
     * @param creationDate: the creation date when the qrcode has been created
     * @param joinCode: the textual join code
     * @param projectId: the project identifier where join with the qrcode
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + JOINING_QRCODES_TABLE +
                    " ("
                    + IDENTIFIER_KEY + ","
                    + CREATION_DATE_KEY + ","
                    + JOIN_CODE_KEY + ","
                    + PROJECT_IDENTIFIER_KEY
                    + ")"
                    + " VALUES ("
                    + ":" + IDENTIFIER_KEY + ","
                    + ":" + CREATION_DATE_KEY + ","
                    + ":" + JOIN_CODE_KEY + ","
                    + ":" + PROJECT_IDENTIFIER_KEY
                    + ")",
            nativeQuery = true
    )
    void insertJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(CREATION_DATE_KEY) long creationDate,
            @Param(JOIN_CODE_KEY) String joinCode,
            @Param(PROJECT_IDENTIFIER_KEY) String projectId
    );

    /**
     *  Method to execute the query to select a {@link JoiningQRCode} by its identifier
     *
     * @param joiningQRCodeId: the identifier of the qrcode to get
     *
     * @return the selected qrcode as {@link JoiningQRCode}
     */
    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

    /**
     *  Method to execute the query to select a {@link JoiningQRCode} by its textual join code
     *
     * @param joinCode: the textual join code of the qrcode to get
     *
     * @return the selected qrcode as {@link JoiningQRCode}
     */
    @Query(
            value = "SELECT * FROM " + JOINING_QRCODES_TABLE + " WHERE " + JOIN_CODE_KEY + "=:" + JOIN_CODE_KEY,
            nativeQuery = true
    )
    JoiningQRCode getJoiningQRCodeByJoinCode(
            @Param(JOIN_CODE_KEY) String joinCode
    );

    /**
     *  Method to execute the query to update an existing {@link JoiningQRCode} after a user join in a project and
     *  the allowed mailing list must remove the joined member
     *
     * @param joiningQRCodeId: the identifier of the qrcode
     * @param projectMembers: the emails of the members
     */
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

    /**
     *  Method to execute the query to delete an existing {@link JoiningQRCode} after that all the member joined in the
     *  project or when the qrcode is expired
     *
     * @param joiningQRCodeId: the identifier of the qrcode to delete
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + JOINING_QRCODES_TABLE + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteJoiningQRCode(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId
    );

    /**
     *  Method to execute the query to delete from an existing {@link JoiningQRCode} an email of a member invited with that
     *  joining qrcode
     *
     * @param joiningQRCodeId: the identifier of the qrcode
     * @param email: the email of the member to remove
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + JOINING_QRCODES_MEMBERS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY +
                    " AND " + EMAIL_KEY + "=:" + EMAIL_KEY,
            nativeQuery = true
    )
    void removeMemberFromMailingList(
            @Param(IDENTIFIER_KEY) String joiningQRCodeId,
            @Param(EMAIL_KEY) String email
    );

}

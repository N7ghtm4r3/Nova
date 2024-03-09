package com.tecknobit.nova.helpers.services.repositories;

import com.tecknobit.nova.records.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.nova.records.User.*;

@Service
@Repository
public interface UsersRepository extends JpaRepository<User, String> {

    @Query(
            value = "SELECT * FROM " + USERS_KEY + " WHERE " + EMAIL_KEY + "=?",
            nativeQuery = true
    )
    User findUserByEmail(
            @Param(EMAIL_KEY) String email
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + PROFILE_PIC_URL_KEY + "=? WHERE "
                    + IDENTIFIER_KEY + "=?",
            nativeQuery = true
    )
    void changeProfilePic(
            @Param(PROFILE_PIC_URL_KEY) String profilePicUrl,
            @Param(IDENTIFIER_KEY) String id
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + EMAIL_KEY + "=? WHERE "
                    + IDENTIFIER_KEY + "=?",
            nativeQuery = true
    )
    void changeEmail(
            @Param(EMAIL_KEY) String newEmail,
            @Param(IDENTIFIER_KEY) String id
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + PASSWORD_KEY + "=? WHERE "
                    + IDENTIFIER_KEY + "=?",
            nativeQuery = true
    )
    void changePassword(
            @Param(PASSWORD_KEY) String newPassword,
            @Param(IDENTIFIER_KEY) String id
    );

}

package com.tecknobit.nova.helpers.services.repositories;

import com.tecknobit.equinox.environment.helpers.services.repositories.EquinoxUsersRepository;
import com.tecknobit.equinox.environment.records.EquinoxUser;
import com.tecknobit.novacore.records.NovaUser;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxUser.*;
import static com.tecknobit.novacore.records.NovaUser.ROLE_KEY;

/**
 * The {@code NovaUsersRepository} interface is useful to manage the queries for the users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see EquinoxUser
 *
 * @since 1.0.1
 */
@Service
@Repository
@Primary
public interface NovaUsersRepository extends EquinoxUsersRepository<NovaUser> {

    /**
     * Method to execute the query to get the potential members for a team
     *
     * @param userId: the identifier of the user to not fetch
     *
     * @return list of potential members as {@link List} of {@link List} of {@link String}
     */
    @Query(
            value = "SELECT " + IDENTIFIER_KEY + "," + PROFILE_PIC_KEY + "," + NAME_KEY + "," + SURNAME_KEY + ","
                    + EMAIL_KEY + "," + ROLE_KEY
                    + " FROM " + USERS_KEY
                    + " WHERE " + IDENTIFIER_KEY + "!=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<List<String>> getPotentialMembers(
            @Param(IDENTIFIER_KEY) String userId
    );

}
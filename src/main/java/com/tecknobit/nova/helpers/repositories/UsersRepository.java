package com.tecknobit.nova.helpers.repositories;

import com.tecknobit.nova.records.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.nova.records.User.EMAIL_KEY;
import static com.tecknobit.nova.records.User.USERS_KEY;

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

}

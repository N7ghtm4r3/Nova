package com.tecknobit.nova.helpers.services.repositories;

import com.tecknobit.nova.records.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.nova.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.records.Project.AUTHOR_KEY;
import static com.tecknobit.nova.records.Project.PROJECT_MEMBERS_TABLE;
import static com.tecknobit.nova.records.User.MEMBER_IDENTIFIER_KEY;
import static com.tecknobit.nova.records.User.PROJECTS_KEY;

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
            value = "SELECT " + PROJECTS_KEY + ".* FROM " + PROJECTS_KEY + " as " + PROJECTS_KEY + " INNER JOIN "
                    + PROJECT_MEMBERS_TABLE + " as " + PROJECT_MEMBERS_TABLE + " ON " + PROJECTS_KEY + "."
                    + IDENTIFIER_KEY + "=" + PROJECT_MEMBERS_TABLE + "." + IDENTIFIER_KEY + " WHERE "
                    + MEMBER_IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    List<Project> getProjects(
            @Param(IDENTIFIER_KEY) String userId
    );

}

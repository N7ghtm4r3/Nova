package com.tecknobit.nova.helpers.services.repositories;

import com.tecknobit.nova.records.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface ProjectsRepository extends JpaRepository<Project, String> {
}

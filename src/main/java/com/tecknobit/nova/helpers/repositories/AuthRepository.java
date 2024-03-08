package com.tecknobit.nova.helpers.repositories;

import com.tecknobit.nova.records.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface AuthRepository extends JpaRepository<User, String> {



}

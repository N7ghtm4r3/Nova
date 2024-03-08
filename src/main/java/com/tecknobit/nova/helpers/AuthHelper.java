package com.tecknobit.nova.helpers;

import com.tecknobit.nova.helpers.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthHelper {

    @Autowired
    private AuthRepository authRepository;



}

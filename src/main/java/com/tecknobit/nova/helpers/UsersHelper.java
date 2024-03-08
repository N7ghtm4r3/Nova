package com.tecknobit.nova.helpers;

import com.tecknobit.nova.helpers.repositories.UsersRepository;
import com.tecknobit.nova.records.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersHelper {

    @Autowired
    private UsersRepository usersRepository;

    public void signUpUser(String id, String token, String name, String surname, String email, String password) {
        usersRepository.save(new User(
                id,
                token,
                name,
                surname,
                email,
                password
        ));
    }

    public User signInUser(String email) {
        return usersRepository.findUserByEmail(email);
    }

    public void deleteUser(String id) {
        usersRepository.deleteById(id);
    }

}

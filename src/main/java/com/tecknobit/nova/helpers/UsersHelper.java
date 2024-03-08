package com.tecknobit.nova.helpers;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.nova.helpers.repositories.UsersRepository;
import com.tecknobit.nova.records.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

import static com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM;

@Service
public class UsersHelper {

    @Autowired
    private UsersRepository usersRepository;

    public void signUpUser(String id, String token, String name, String surname, String email, String password) throws NoSuchAlgorithmException {
        usersRepository.save(new User(
                id,
                token,
                name,
                surname,
                email,
                hash(password)
        ));
    }

    public User signInUser(String email, String password) throws NoSuchAlgorithmException {
        User user = usersRepository.findUserByEmail(email);
        if(user != null && user.getPassword().equals(hash(password)))
            return user;
        return null;
    }

    public void deleteUser(String id) {
        usersRepository.deleteById(id);
    }

    private String hash(String secret) throws NoSuchAlgorithmException {
        return APIRequest.base64Digest(secret, SHA256_ALGORITHM);
    }

}

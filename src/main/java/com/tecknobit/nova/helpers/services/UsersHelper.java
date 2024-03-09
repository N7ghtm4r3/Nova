package com.tecknobit.nova.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.nova.helpers.services.repositories.UsersRepository;
import com.tecknobit.nova.records.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM;
import static com.tecknobit.nova.helpers.ResourcesProvider.IMAGES_PATH;
import static com.tecknobit.nova.helpers.ResourcesProvider.PROFILES_DIRECTORY;

@Service
public class UsersHelper {

    @Autowired
    private UsersRepository usersRepository;

    public void signUpUser(String id, String token, String name, String surname, String email,
                           String password) throws NoSuchAlgorithmException {
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

    public String changeProfilePic(MultipartFile profilePic, String userId) throws IOException {
        String suffix = Objects.requireNonNull(profilePic.getResource().getFilename()).split("\\.")[1];
        File file = new File(IMAGES_PATH + PROFILES_DIRECTORY + "/" + userId + "." + suffix);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(profilePic.getBytes());
        }
        String profilePicPath = file.getPath().replaceAll("\\\\", "/").replace(IMAGES_PATH, "");
        usersRepository.changeProfilePic(profilePicPath, userId);
        return profilePicPath;
    }

    public void changeEmail(String newEmail, String userId) {
        usersRepository.changeEmail(newEmail, userId);
    }

    public void changePassword(String newPassword, String userId) throws NoSuchAlgorithmException {
        usersRepository.changePassword(hash(newPassword), userId);
    }

    public void deleteUser(String id) {
        usersRepository.deleteById(id);
    }

    private String hash(String secret) throws NoSuchAlgorithmException {
        return APIRequest.base64Digest(secret, SHA256_ALGORITHM);
    }

}

package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final HashService hashService;

    public UserService(final UserMapper userMapper,
                       final HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public User getUserByUsername(final String username) {
        return userMapper.getUserByUsername(username);
    }

    public boolean userExistsWithUsername(final String username) {
        return userMapper.userExistsWithUsername(username);
    }

    public boolean isUsernameValid(final String username) {
        return userMapper.getUserByUsername(username) == null;
    }

    public int createUser(final User user) {
        SecureRandom random = new SecureRandom();
        byte[] saltByte = new byte[16];
        random.nextBytes(saltByte);
        String salt = Base64.getEncoder().encodeToString(saltByte);
        String password =
                hashService.getHashedValue(user.getPassword(), salt);
        return userMapper.insert(new User(
                0, // Unused value
                user.getUsername(),
                salt,
                password,
                user.getFirstName(),
                user.getLastName()
        ));
    }
}

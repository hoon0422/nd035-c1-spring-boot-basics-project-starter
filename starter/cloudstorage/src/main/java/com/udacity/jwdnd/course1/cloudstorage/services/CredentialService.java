package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.InvalidValueException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.EntityNotFoundException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity.UserNotFoundWithUsernameException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public CredentialService(
            final CredentialMapper credentialMapper,
            final UserService userService,
            final EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getCredentialsOfUser(final String username,
                                                 final boolean withDecryption) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        List<Credential> credentials =
                credentialMapper.getCredentialsOfUser(user.getUserId());

        if (withDecryption) {
            credentials = credentials.stream().peek(c -> {
                c.setPassword(encryptionService.decryptValue(
                        c.getPassword(), c.getKey()
                ));
                c.setKey("");
            }).collect(Collectors.toList());
        }

        return credentials;
    }

    public Credential getCredentialById(final String username,
                                        final Integer credentialId,
                                        final Boolean withDecryption) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }
        Credential credential =
                credentialMapper.getCredentialById(credentialId);
        if (credential == null) {
            return null;
        }
        if (!credential.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Credential with the ID "
                            + "does not belong to the current user.");
        }

        if (withDecryption) {
            String decryptedPassword = encryptionService
                    .decryptValue(credential.getPassword(),
                            credential.getKey());
            credential.setPassword(decryptedPassword);
            credential.setKey("");
        }
        return credential;
    }

    public int createCredential(final String username,
                                final Credential credential) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        if (credential.getUsername().equals("")) {
            throw new InvalidValueException(
                    "Credential username cannot be an empty string.");
        }
        if (credential.getUrl().equals("")) {
            throw new InvalidValueException(
                    "Credential URL cannot be an empty string.");
        }
        if (credential.getPassword().equals("")) {
            throw new InvalidValueException(
                    "Credential password cannot be an empty string.");
        }
        String key = createKey();
        String encryptedPassword =
                encryptionService.encryptValue(credential.getPassword(), key);

        return credentialMapper.insert(new Credential(
                0,
                credential.getUrl(),
                credential.getUsername(),
                key,
                encryptedPassword,
                user.getUserId()
        ));
    }

    public int updateCredential(final String username,
                                final Credential credential) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        Credential credentialInDB = credentialMapper
                .getCredentialById(credential.getCredentialId());
        if (credentialInDB == null) {
            throw new EntityNotFoundException(
                    "Credential with the ID is not found.");
        }
        if (!credentialInDB.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Credential with the ID "
                            + "does not belong to the current user.");
        }

        if (credential.getUsername().equals("")) {
            throw new InvalidValueException(
                    "Credential username cannot be an empty string.");
        }
        if (credential.getUrl().equals("")) {
            throw new InvalidValueException(
                    "Credential URL cannot be an empty string.");
        }
        if (credential.getPassword().equals("")) {
            throw new InvalidValueException(
                    "Credential password cannot be an empty string.");
        }
        credentialInDB.setUsername(credential.getUsername());
        credentialInDB.setUrl(credential.getUrl());
        credentialInDB.setPassword(
                encryptionService.encryptValue(credential.getPassword(),
                        credentialInDB.getKey()));

        return credentialMapper.update(credentialInDB);
    }

    public void deleteCredential(final String username,
                                 final Integer credentialId) {

        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundWithUsernameException();
        }

        Credential credentialInDB = credentialMapper
                .getCredentialById(credentialId);
        if (credentialInDB == null) {
            throw new EntityNotFoundException(
                    "Credential with the ID is not found.");
        }
        if (!credentialInDB.getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Credential with the ID "
                            + "does not belong to the current user.");
        }

        credentialMapper.delete(credentialId);
    }

    private String createKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}

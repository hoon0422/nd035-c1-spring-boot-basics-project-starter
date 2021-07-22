package com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity;

public class UserNotFoundWithUsernameException extends EntityNotFoundException {
    public UserNotFoundWithUsernameException() {
        super("User not found with the given username.");
    }
}

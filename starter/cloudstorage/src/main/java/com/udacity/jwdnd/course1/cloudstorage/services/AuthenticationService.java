package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthenticationService implements AuthenticationProvider {
    private final UserService userService;
    private final HashService hashService;

    public AuthenticationService(final UserService userService,
                                 final HashService hashService) {
        this.userService = userService;
        this.hashService = hashService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        String hashedPassword =
                hashService.getHashedValue(password, user.getSalt());
        if (!user.getPassword().equals(hashedPassword)) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(username, password,
                new ArrayList<>());
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return aClass == UsernamePasswordAuthenticationToken.class;
    }
}

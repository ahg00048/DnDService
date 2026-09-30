package es.ujaen.ahg00048.microservice_user.security;

import es.ujaen.ahg00048.microservice_user.exception.UserRegistrationException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.ujaen.ahg00048.microservice_user.service.UserService;

import java.util.Collections;

@Service
public class CredentialsService implements UserDetailsService {
    @Autowired
    private UserService _service;


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        es.ujaen.ahg00048.microservice_user.entity.User user = null;

        try {
            user = _service.getUser(username);
        } catch (UserRegistrationException e) {
            throw new UsernameNotFoundException("");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}

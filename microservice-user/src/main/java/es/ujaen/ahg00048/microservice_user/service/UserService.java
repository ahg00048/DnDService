package es.ujaen.ahg00048.microservice_user.service;

import es.ujaen.ahg00048.microservice_user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.*;


@Service
@NoArgsConstructor
public class UserService {
    @Autowired
    private UserRepository _usersRep;

    public final User admin = new User("random@gmail.com", "admin", "secret");


    public List<User> getUsers(@Valid User user) throws UserAuthorizationException {
        if (!user.equals(admin))
            throw new UserAuthorizationException();

        return _usersRep.findAll();
    }

    public User login(String email, String password) throws UserAuthenticationException, UserRegistrationException {
        User user = _usersRep.findById(email).orElseThrow(UserRegistrationException::new); // User not registered

        if (!user.getPassword().equals(password)) // Wrong password
            throw new UserAuthenticationException();

        return user;
    }

    public User addUser(@Valid User newUser) throws UserRegistrationException {
        if (_usersRep.existsById(newUser.getEmail())) // User registered
            throw new UserRegistrationException();

        return _usersRep.insert(newUser);
    }

    public void removeUser(@Valid User caller, String email) throws UserRegistrationException, UserAuthorizationException {
        Optional<User> user = _usersRep.findById(caller.getEmail());
        if (user.isEmpty())
            if (!caller.equals(admin))
                throw new UserRegistrationException();
            else
                user = Optional.of(admin);

        if (!_usersRep.existsById(email))
            throw new UserRegistrationException();

        if (!user.get().getEmail().equals(email) && !user.get().equals(admin))
            throw new UserAuthorizationException();

        _usersRep.delete(user.get());
    }
}

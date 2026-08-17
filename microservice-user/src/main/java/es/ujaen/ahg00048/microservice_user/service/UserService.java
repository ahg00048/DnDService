package es.ujaen.ahg00048.microservice_user.service;

import es.ujaen.ahg00048.microservice_user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.*;


@Service
@NoArgsConstructor
public class UserService {
    @Autowired
    private UserRepository _usersRep;

    @Autowired
    private Environment _env;

    private User _admin;

    @PostConstruct
    public void initialize() {
        _admin = new User(_env.getProperty("admin.email"), "admin", _env.getProperty("admin.pwd"));
    }

    public User getUser(String email) throws UserRegistrationException {
        if (email.equals(_admin.getEmail()))
            return _admin;

        return _usersRep.findById(email).orElseThrow(UserRegistrationException::new); // User not registered;
    }

    public List<User> getUsers(@Valid User user) throws UserAuthorizationException {
        if (!user.equals(_admin))
            throw new UserAuthorizationException();

        return _usersRep.findAll();
    }

    public User login(String email, String password) throws UserAuthenticationException, UserRegistrationException {
        User user = null;
        if (email.equals(_admin.getEmail()) && password.equals(_admin.getPassword()))
            user = _admin;
        else
            user = _usersRep.findById(email).orElseThrow(UserRegistrationException::new); // User not registered

        if (!user.getPassword().equals(password)) // Wrong password
            throw new UserAuthenticationException();

        return user;
    }

    public void addUser(@Valid User newUser) throws UserRegistrationException {
        if (_usersRep.existsById(newUser.getEmail())) // User registered
            throw new UserRegistrationException();

        _usersRep.insert(newUser);
    }

    public void removeUser(@Valid User caller, String email) throws UserRegistrationException, UserAuthorizationException {
        Optional<User> user = _usersRep.findById(caller.getEmail());
        if (user.isEmpty())
            if (!caller.equals(_admin))
                throw new UserRegistrationException();
            else
                user = Optional.of(_admin);

        if (!_usersRep.existsById(email))
            throw new UserRegistrationException();

        if (!user.get().getEmail().equals(email) && !user.get().equals(_admin))
            throw new UserAuthorizationException();

        _usersRep.deleteById(email);
    }

    public void changePassword(@Valid User caller, String newPassword) throws UserRegistrationException, UserBadOperation {
        User user = _usersRep.findById(caller.getEmail()).orElseThrow(UserRegistrationException::new);

        user.setPassword(newPassword);
        _usersRep.save(user);
    }
}

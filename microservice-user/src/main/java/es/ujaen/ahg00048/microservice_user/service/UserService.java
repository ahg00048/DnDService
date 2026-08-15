package es.ujaen.ahg00048.microservice_user.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import es.ujaen.ahg00048.microservice_user.entity.User;
import es.ujaen.ahg00048.microservice_user.exception.*;

@Service
public class UserService {
    // @Autowired
    private final Map<String, User> _users = new HashMap<>();

    public final User admin = new User("random@gmail.com", "admin", "secret");


    public UserService() {

    }


    public User login(String email, String password) throws UserAuthenticationException, UserRegistrationException {
        if (!_users.containsKey(email)) // User not registered
            throw new UserRegistrationException();

        User user = _users.get(email);
        if (!user.getPassword().equals(password)) // Wrong password
            throw new UserAuthenticationException();

        return user;
    }

    public User addUser(@Valid User user) throws UserRegistrationException {
        if (_users.containsKey(user.getEmail())) // User not registered
            throw new UserRegistrationException();

        _users.put(user.getEmail(), user);

        return _users.get(user.getEmail());
    }

    public void removeUser(@Valid User user, String email) throws UserRegistrationException, UserAuthorizationException {
        if ((!_users.containsKey(user.getEmail()) && !user.equals(admin)) || !_users.containsKey(email))
            throw new UserRegistrationException();

        if (!user.getEmail().equals(email) && !user.equals(admin))
            throw new UserAuthorizationException();

        _users.remove(email);
    }
}

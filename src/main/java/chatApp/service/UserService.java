package chatApp.service;

import chatApp.Entities.Guest;
import chatApp.Entities.User;
import chatApp.repository.GuestRepository;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    private Map<String, User> registeredUsers = new HashMap<>();

    public UserService() {
        loadAllRegisteredUsers();
    }


    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            System.out.println(String.format("Email %s exists in users table", user.getEmail()));
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        return userRepository.save(user);
    }

    public Guest addUser(Guest guest) throws SQLDataException {
        if (guestRepository.findByNickname(guest.getNickName()) != null) {
            System.out.println(String.format("Nickname %s exists in guests table", guest.getNickName()));
            throw new SQLDataException(String.format("Nickname %s exists in guests table",  guest.getNickName()));
        }
        return guestRepository.save(guest);
    }

    public void loadAllRegisteredUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        for (User user : users)
            this.registeredUsers.put(user.getEmail(), user);
    }
}

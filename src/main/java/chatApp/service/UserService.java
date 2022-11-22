package chatApp.service;

import chatApp.Entities.Guest;
import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;
import chatApp.repository.GuestRepository;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    private Map<Integer, String> tokens;
    private Map<String, User> registeredUsers = new HashMap<>();

    public UserService(UserRepository userRepository, GuestRepository guestRepository) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        tokens = new HashMap<>();
        //  loadAllRegisteredUsers();
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

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public Guest addUser(Guest guest) throws SQLDataException {
        if (guestRepository.findByNickName(guest.getNickName()) != null) {
            System.out.println(String.format("Nickname %s exists in guests table", guest.getNickName()));
            throw new SQLDataException(String.format("Nickname %s exists in guests table", guest.getNickName()));
        }
        return guestRepository.save(guest);
    }

    public void loadAllRegisteredUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        for (User user : users)
            this.registeredUsers.put(user.getEmail(), user);
    }


    /*
     * we can get user once
     */
    public String login(SubmitedUser user) {
        if (isUserValid(user)) {
            int userId = userRepository.findByEmail(user.getEmail()).getId();
            String token = generateRandomToken();
            tokens.put(userId, token);
            return token;
        }
        return null;
    }

    private boolean isUserValid(SubmitedUser user) {
        return isUserExistedAndPasswordIsFit(user);
    }

    private boolean isUserExistedAndPasswordIsFit(SubmitedUser user) {
        User myUser = userRepository.findByEmail(user.getEmail());

        if (myUser != null & myUser.getPassword().equals(user.getPassword())) {
            return true;
        }

        return false;
    }

    private String generateRandomToken() {
        int token = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        return String.valueOf(token);
    }

    public List<Guest> getGuestList() {
        return guestRepository.findAll();
    }
}

package chatApp.service;

import chatApp.Entities.Guest;
import chatApp.Entities.Response;
import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;
import chatApp.repository.GuestRepository;
import chatApp.repository.UserRepository;
import chatApp.util.ValidationUtils;
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
    public Response addUser(SubmitedUser user) throws SQLDataException {

        if (!ValidationUtils.validateEmail(user.getEmail()))
            return new Response(400, "Invalid email !");

        if (!ValidationUtils.validatePassword(user.getPassword()))
            return new Response(400, "Invalid user password , password should be more than 8 characters !");

        if (!ValidationUtils.validateName(user.getNickName()))
            return new Response(400, "Invalid nickName , nickName should be more than 3 characters !");

        if (userRepository.findByEmail(user.getEmail()) != null)
            return new Response(400, "User is already existed !");

        User myUser = new User.Builder(user.getEmail(), user.getPassword(), user.getNickName()).build();
        if (userRepository.save(myUser) != null)
            return new Response(200, "User is registered successfully");

        return null;
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
        System.out.println("user in login fun : " + user);
        if (isUserValid(user)) {
            int userId = userRepository.findByEmail(user.getEmail()).getId();
            String token = generateRandomToken();
            tokens.put(userId, token);
            userRepository.updateUserSetStatusForId("online", userId);
            System.out.println(token);
            return token;
        }
        return null;
    }

    private boolean isUserValid(SubmitedUser user) {
        return isUserExistedAndPasswordIsFit(user);
    }

    private boolean isUserExistedAndPasswordIsFit(SubmitedUser user) {
        User myUser = userRepository.findByEmail(user.getEmail());

        if (myUser != null & myUser.getPassword().equals(user.getPassword()))
            return true;

        return false;
    }

    private String generateRandomToken() {
        int token = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        return String.valueOf(token);
    }

    /*
     * Registered user sends his token , if its available we could make logout for him , otherwise we send an error message .
     * Guest sends his name , so we delete him from Guests DB .
     * If the token is numeric it means it's a registered user ,else if it's not , so he is a guest and it's his name .
     */
    public boolean logout(String token) {

        if (ValidationUtils.isNumeric(token)) {
            for (int id : tokens.keySet())
                if (tokens.get(id).equals(token)) {
                    tokens.remove(id);
                    userRepository.updateUserSetStatusForId("offline", id);
                    return true;
                }
            return false;
        }

        if (guestRepository.deleteUserByNickName(token) < 0)
            return false;

        return true;
    }

}




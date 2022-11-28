package chatApp.service;

import chatApp.Entities.*;
import chatApp.repository.GuestRepository;
import chatApp.repository.UserRepository;
import chatApp.util.EmailActivation;
import chatApp.util.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    private Map<Integer, String> tokens;
    private Map<String, User> registeredUsers = new HashMap<>();
    public Map<String, SubmitedUser> useresCode = new HashMap<>();

    public Map<String, SubmitedUser> getUseresCode() {
        return useresCode;
    }

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
            throw new SQLDataException(String.format("Email \" %s \" is not valid!", user.getEmail()));

        if (!ValidationUtils.validatePassword(user.getPassword()))
            throw new SQLDataException(String.format("Password \" %s \" is not valid!", user.getPassword()));

        if (!ValidationUtils.validateName(user.getNickName()))
            throw new SQLDataException(String.format("Nickname \" %s \" is not valid!", user.getNickName()));

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new SQLDataException(String.format("Email \" %s \" is Already Exist!", user.getEmail()));

        String code = ValidationUtils.generateRandomToken();
        useresCode.put(code, user);
        EmailActivation.sendEmailWithGenerateCode(code, user);

        return new Response(200, "Activate your email to complete the registration process !");
    }


    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public Response saveProfile(User user) throws SQLDataException {

        if (!ValidationUtils.validateName(user.getNickName()))
            throw new SQLDataException(String.format("Nickname \" %s \" is not valid!", user.getNickName()));
        if (!ValidationUtils.validateName(user.getFirstName()))
            throw new SQLDataException(String.format("First Name \" %s \" is not valid!", user.getFirstName()));
        if (!ValidationUtils.validateName(user.getLastName()))
            throw new SQLDataException(String.format("Last Name \" %s \" is not valid!", user.getLastName()));
        System.out.println("new profile :"+user);
        user.setPassword(userRepository.findByEmail(user.getEmail()).getPassword());
        user.setRole(userRepository.findByEmail(user.getEmail()).getRole());
        user.setIsMuted(userRepository.findByEmail(user.getEmail()).getIsMuted());

        userRepository.delete(userRepository.findByEmail(user.getEmail()));
        userRepository.save(user);
        return new Response(200, "new profile saved successfully!");
    }




    public Response enterUserToDB(String code) throws NoSuchAlgorithmException {
        SubmitedUser user = useresCode.get(code);

        if (user == null) {
            return new Response(400, "User that you are trying to validat is not existed");
        }

        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
        if (userRepository.save(myUser) != null) {
            EmailActivation.sendSuccessRegisterationMessageToUser(user);
            return new Response(200, "User is registered successfully");
        }

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
    public String login(SubmitedUser user) throws NoSuchAlgorithmException {
        System.out.println("user in login fun : " + user);
        if (isUserValid(user)) {
            int userId = userRepository.findByEmail(user.getEmail()).getId();
            String token = ValidationUtils.generateRandomToken();
            tokens.put(userId, token);
            userRepository.updateUserSetStatusForId("online", userId);
            System.out.println(token);
            return token;
        }
        return null;
    }

    private boolean isUserValid(SubmitedUser user) throws NoSuchAlgorithmException {
        return isUserExistedAndPasswordIsFit(user);
    }

    private boolean isUserExistedAndPasswordIsFit(SubmitedUser user) throws NoSuchAlgorithmException {
        User myUser = userRepository.findByEmail(user.getEmail());
        if (myUser == null)
            return false;
        else if (myUser.getPassword().equals(ValidationUtils.secretPassword(user.getPassword())))
            return true;
        return false;
    }


    /*
     * Registered user sends his token , if its available we could make logout for him , otherwise we send an error message .
     * Guest sends his name , so we delete him from Guests DB .
     * If the token is numeric it means it's a registered user ,else if it's not , so he is a guest and it's his name .
     */
    public boolean logout(String token) {
        Gson g = new Gson();
        Token t = g.fromJson(token, Token.class);
        String mytoken = t.getToken();
        System.out.println(mytoken);
        if (ValidationUtils.isNumeric(mytoken)) {
            for (int id : tokens.keySet())
                if (tokens.get(id).equals(mytoken)) {
                    tokens.remove(id);
                    userRepository.updateUserSetStatusForId("offline", id);
                    return true;
                }
            return false;
        }
        return false;

    }


    public boolean logoutGuest(String nickName) {

        return guestRepository.deleteUserByNickName(nickName) >= 0;
    }

    public List<Guest> getGuestList() {
        return guestRepository.findAll();
    }

    public List<User> getUserList() {
        List<User> userList = userRepository.findAll();
        List<User> res = new ArrayList<>();
        for (User u : userList) {
            if (!Objects.equals(u.getStatus(), "offline"))////////////////
                res.add(u);
        }
        return res;
    }

    public User getUserByToken(String token) {
        Integer myid = -1;
        Gson g = new Gson();
        Token t = g.fromJson(token, Token.class);

        for (Integer key : tokens.keySet()) {
            if (tokens.get(key).equals(t.getToken()))
                myid = key;
        }
        User result = userRepository.findUserById(myid);
        return result;
    }

    public User getUserById(String id) {
        System.out.println(id);
        Gson g = new Gson();
        Token t = g.fromJson(id, Token.class);
        System.out.println(t);
        User result = userRepository.findUserById(Integer.valueOf(t.getToken()));
        return result;
    }

}

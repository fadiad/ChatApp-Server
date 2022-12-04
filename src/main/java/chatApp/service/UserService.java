package chatApp.service;

import chatApp.Entities.*;
import chatApp.repository.ActivateRepository;
import chatApp.repository.GuestRepository;
import chatApp.repository.UserRepository;
import chatApp.util.EmailActivation;
import chatApp.util.ValidationUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ActivateRepository activateRepository;
    private Map<Integer, String> usersTokens;
    private Map<String, String> guestsTokens;

    private Map<String, User> registeredUsers = new HashMap<>();
    public Map<String, SubmitedUser> useresCode = new HashMap<>();

    public Map<String, SubmitedUser> getUseresCode() {
        return useresCode;
    }

    public UserService(UserRepository userRepository, GuestRepository guestRepository) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        usersTokens = new HashMap<>();
        guestsTokens = new HashMap<>();
    }


    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws IllegalArgumentException when the provided email already exists
     */
//    public Response addUser(SubmitedUser user) throws IllegalArgumentException {
//
//        if (userRepository.findByEmail(user.getEmail()) != null)
//            throw new IllegalArgumentException(String.format("Email \" %s \" is Already Exist!", user.getEmail()));
//
//        String code = ValidationUtils.generateRandomToken();
//        useresCode.put(code, user);
//        EmailActivation.sendEmailWithGenerateCode(code, user);
//
//        return new Response(200, "Activate your email to complete the registration process !");
//    }


    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws IllegalArgumentException when the provided email already exists
     */
    public Response saveProfile(User user) throws IllegalArgumentException {

        if (!ValidationUtils.validateName(user.getNickName()))
            throw new IllegalArgumentException(String.format("Nickname \" %s \" is not valid!", user.getNickName()));
        if (!ValidationUtils.validateName(user.getFirstName()))
            throw new IllegalArgumentException(String.format("First Name \" %s \" is not valid!", user.getFirstName()));
        if (!ValidationUtils.validateName(user.getLastName()))
            throw new IllegalArgumentException(String.format("Last Name \" %s \" is not valid!", user.getLastName()));
        System.out.println("new profile :" + user);
        user.setPassword(userRepository.findByEmail(user.getEmail()).getPassword());
        user.setRole(userRepository.findByEmail(user.getEmail()).getRole());
        user.setIsMuted(userRepository.findByEmail(user.getEmail()).getIsMuted());
        User u = userRepository.findByEmail(user.getEmail());
        userRepository.delete(u);
        userRepository.save(user);
        userRepository.updateId(u.getId(), u.getEmail());

        return new Response(200, "new profile saved successfully!");
    }


    public Response enterUserToDB(String code) throws NoSuchAlgorithmException,IllegalArgumentException {
        ActiveUser user = activateRepository.findByCode(code);
        System.out.println("-----------------Enter User to DB------------------");
        System.out.println(user);
        if (user == null) {
            throw new IllegalArgumentException(String.format("Your Code is not Valid!"));
        }
        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
        if (userRepository.save(myUser) != null) {
            EmailActivation.sendSuccessRegisterationMessageToUser(user);
            activateRepository.delete(user);
            return new Response(200, "User is registered successfully");

        }

        return null;
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public String addGuest(Guest SubmittedGuest) throws IllegalArgumentException {
        if (guestRepository.findByNickName(SubmittedGuest.getNickName()) != null) {
            System.out.println(String.format("Nickname %s exists in guests table", SubmittedGuest.getNickName()));
            throw new IllegalArgumentException(String.format("Nickname %s exists in guests table", SubmittedGuest.getNickName()));
        }

        Guest savedGuest = guestRepository.save(SubmittedGuest);

        if (savedGuest != null) {
            String token = ValidationUtils.generateRandomToken();
            guestsTokens.put(savedGuest.getNickName(), token);
            return token;
        }

        return null;
    }

    /*
     * we can get user once
     */
    public String login(SubmitedUser user) throws NoSuchAlgorithmException , IllegalArgumentException{
        System.out.println("user in login fun : " + user);
        if (isUserValid(user)) {
            int userId = userRepository.findByEmail(user.getEmail()).getId();
            String token = ValidationUtils.generateRandomToken();
            usersTokens.put(userId, token);
            System.out.println("usersToken : " + usersTokens);
            userRepository.updateUserSetStatusForId("online", userId);
            System.out.println(token);
            return token;
        }
        return null;
    }

    private boolean isUserValid(SubmitedUser user) throws NoSuchAlgorithmException, IllegalArgumentException {
        return isUserExistedAndPasswordIsFit(user);
    }

    private boolean isUserExistedAndPasswordIsFit(SubmitedUser user) throws IllegalArgumentException, NoSuchAlgorithmException {
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
            for (int id : usersTokens.keySet())
                if (usersTokens.get(id).equals(mytoken)) {
                    usersTokens.remove(id);
                    userRepository.updateUserSetStatusForId("offline", id);
                    return true;
                }
            return false;
        }
        return false;

    }


    public boolean logoutGuest(String token) {
        System.out.println("---------logout Guest---------");
        System.out.println(token);
        Gson g = new Gson();
        Token t = g.fromJson(token, Token.class);
        System.out.println(t);
        System.out.println(t.getToken());


        for (String nickName : guestsTokens.keySet()) {
            if (guestsTokens.get(nickName).equals(t.getToken())) {
                int delresult = guestRepository.deleteUserByNickName(nickName);
                if (delresult > 0) {
                    guestsTokens.remove(nickName);
                    return true;
                }
            }
        }

        return false;
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
//        Gson g = new Gson();
//        Token t = g.fromJson(token, Token.class);

        for (Integer key : usersTokens.keySet()) {
            if (usersTokens.get(key).equals(token))
                myid = key;
        }
        User result = userRepository.findUserById(myid);
        return result;
    }


    public Response mute(String token, String id) {
        System.out.println("----------muting---------");
        if (isAdmin(token)) {
            int i = Integer.parseInt(id);
            userRepository.mute(i);
            System.out.println("user after muting" + userRepository.findAll());
            return new Response(200, "User is muted!");
        }

        return new Response(404, "Can't mute user!");
    }

    public Response muteGuest(String token, String nickName) {
        System.out.println("----------muteGuest---------");
        if (isAdmin(token)) {
            guestRepository.mute(nickName);
            System.out.println("user after muting" + guestRepository.findAll());
            return new Response(200, "User is muted!");
        }

        return new Response(404, "Can't mute user!");
    }

    public Response unMute(String token, String id) {
        System.out.println("----------unMuting---------");
        if (isAdmin(token)) {
            int i = Integer.parseInt(id);
            userRepository.unMute(i);
            System.out.println("user after unMuting" + userRepository.findAll());
            return new Response(200, "User is un muted!");
        }
        return new Response(404, "Can't unmute user!");
    }


    public Response unmuteGuest(String token, String nickName) {
        System.out.println("----------unmuteGuest---------");
        if (isAdmin(token)) {
            guestRepository.unMute(nickName);
            System.out.println("user after unMuting" + guestRepository.findAll());
            return new Response(200, "User is un muted!");
        }
        return new Response(404, "Can't unmute user!");
    }

    private boolean isAdmin(String token) {
        System.out.println("usersTokens = " + usersTokens);

        Gson g = new Gson();
        Token t = g.fromJson(token, Token.class);
        String mytoken = t.getToken();

        for (int id : usersTokens.keySet()) {
            if (usersTokens.get(id).equals(mytoken)) {
                User user = userRepository.findUserById(id);
                if (user.getRole() == 1)
                    return true;
            }
        }
        System.out.println("not admin");
        return false;
    }


    public boolean isUserMuted(String token) {
        System.out.println("the token  : " + token);

        if (isRegisteredUserMuted(token) || isGuestMuted(token))
            return true;

        return false;
    }

    private boolean isRegisteredUserMuted(String token) {
        for (Integer id : usersTokens.keySet()) {
            if (usersTokens.get(id).equals(token)) {
                User user = userRepository.findUserById(id);
                System.out.println("***: " + id);
                System.out.println("***: " + user);
                if (user.getIsMuted())
                    return true;
            }
        }
        return false;
    }

    private boolean isGuestMuted(String token) {
        for (String nickName : guestsTokens.keySet()) {
            System.out.println("token : " + token);
            if (guestsTokens.get(nickName).equals(token)) {
                Guest guest = guestRepository.findByNickName(nickName);
                if (guest.isMuted())
                    return true;
            }
        }
        return false;
    }


    public User getUserById(String id) {
        System.out.println(id);
        Gson g = new Gson();
        Token t = g.fromJson(id, Token.class);
        System.out.println(t);
        User result = userRepository.findUserById(Integer.valueOf(t.getToken()));
        return result;
    }


//    public boolean addForTest(SubmitedUser user) throws NoSuchAlgorithmException {
//        if (user == null) {
//            return false;
//        }
//
//        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
//        if (userRepository.save(myUser) != null) {
//            EmailActivation.sendSuccessRegisterationMessageToUser(user);
//            return true;
//        }
//        return false;
//    }
}

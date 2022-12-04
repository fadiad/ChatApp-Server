package chatApp.controller;

import chatApp.Entities.*;
import chatApp.service.ChatService;
import chatApp.service.UserService;
import chatApp.util.ValidationUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LogManager.getLogger(UserController.class.getName());
    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;


    /**
     * @param user details
     * @return check validations and return the token of the user if he succeeded to log in
     * @throws IllegalArgumentException validations problems
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody SubmitedUser user) throws IllegalArgumentException, NoSuchAlgorithmException {
        System.out.println("------------Registered User login-------------");
        System.out.println(user);
        String token = "";

        if (ValidationUtils.loginUserValidation(user)) {
            token = userService.login(user);
            logger.debug(token);
        }

        if (token == null || token.isEmpty()){
            logger.error("email or password not correct");
            throw new IllegalArgumentException(String.format("email or password is not correct !"));

        }

        return ResponseEntity.ok(token);
    }


    /**
     * @param user of a guest
     * @return save the guest and the token
     * @throws IllegalArgumentException validations problems
     */
    @RequestMapping(value = "loginGuest", method = RequestMethod.POST) //TODO: problem
    public ResponseEntity<String> loginGuest(@RequestBody SubmitedUser user) throws IllegalArgumentException {
        System.out.println("------------guest login-------------");
        if (ValidationUtils.guestValidation(user)) {
            Guest guest = new Guest(user.getNickName());
            //It is need to be a guest need to send only name
            logger.debug(guest);
            return ResponseEntity.status(HttpStatus.OK).body(userService.addGuest(guest));
        }
        logger.error("Exception, nickName not valid (length name)");
        throw new IllegalArgumentException(String.format("Nickname \" %s \" is not valid!", user.getNickName()));
    }

    /**
     * @param user details
     * @return check the user details, validation, and send a email with code,
     * then, keep him on map until he activates his email.
     * @throws IllegalArgumentException if there is a validated problems
     */
    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody SubmitedUser user) throws IllegalArgumentException {
        Response response = userService.addUser(user); //It is a user need to send full user
        Gson g = new Gson();
        return ResponseEntity
                .status(response.getStatus())
                .body(g.toJson(response.getMessage()));
    }


    /**
     * @param user details.
     * @return update the user profile.
     * @throws IllegalArgumentException if one of the details not valid.
     */
    @RequestMapping(value = "saveProfile", method = RequestMethod.POST)
    public ResponseEntity<String> saveProfile(@RequestBody User user) throws IllegalArgumentException {

        System.out.println("in controller: " + user);
        Response response = userService.saveProfile(user);
        Gson g = new Gson();
        return ResponseEntity
                .status(response.getStatus())
                .body(g.toJson(response.getMessage()));
    }


    /**
     * @param user guest details (name)
     * @return response 200 if he found the guest, he deletes him. else error.
     */
    @RequestMapping(value = "logoutGuest", method = RequestMethod.POST) //TODO: problem
    public ResponseEntity<String> logoutGuest(@RequestBody SubmitedUser user) {
        if (!userService.logoutGuest(user.getNickName())) {
            logger.error("user not found, cant log out");
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("some error !");
    }
        return ResponseEntity.ok("logout done successfully");
    }

    /**
     * @param token of the online user
     * @return response 200 if he found the token in the map, he log out the user. else error
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST) //TODO: problem
    public ResponseEntity<String> logout(@RequestBody String token) {
        logger.info(token);
        if (!userService.logout(token)) {
            logger.error("cant log out, not found");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("some error !");
        }
        return ResponseEntity.ok("logout done successfully");
    }

    /**
     * @param code that we send to the mail.
     * @return if the code is right the func enter the user to the users repo/
     */
    @RequestMapping(value = "activate", method = RequestMethod.GET)
    public ResponseEntity<Object> activateEmail(@RequestParam String code) throws NoSuchAlgorithmException {
        System.out.println("------------Activate account after login-------------");
        System.out.println(code);
        logger.info(code);
        Response response = userService.enterUserToDB(code);
        logger.info(response.getStatus());
        return ResponseEntity
                .status(response.getStatus())
                .body(response.getMessage());
    }


    /**
     * @return list of all the guest users from the repo.
     */
    @RequestMapping(value = "onlineGuestUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Guest>> getGuestList()  {
        System.out.println("------------online Users-------------");
        List<Guest> mylist = userService.getGuestList();
        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }


    /**
     * @param token of online user
     * @return the user from the map of the online users in the service
     */
    @RequestMapping(value = "userByToken", method = RequestMethod.GET) //TODO: problem
    public ResponseEntity<User> getUserByToken1(@RequestParam String token) {
        System.out.println("------------User By Token-------------");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token));
    }

//    @RequestMapping(value = "userByToken", method = RequestMethod.POST)
//    public ResponseEntity<User> getUserByToken(@RequestParam String token) throws SQLDataException {
//        System.out.println("------------User By Token-------------");
//        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token));
//    }


    /**
     * @param id of the user
     * @return search the user by id in the repo, and return the user in the response body
     */
    @RequestMapping(value = "userById", method = RequestMethod.POST) //TODO: problem
    public ResponseEntity<User> getUserById(@RequestBody String id)  {
        System.out.println("------------User By ID-------------");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    /**
     * @return list of 'online' user from the user repo.
     */
    @RequestMapping(value = "onlineUsers", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserList() {
        System.out.println("------------online Users-------------");
//        List<User> mylist = userService.getUserList();

        List<User> mylist = userService.getUserList().stream().filter(user -> user.getStatus() != "offline")
                .sorted(Comparator.comparing(User::getRole))
                .collect(Collectors.toList());

        System.out.println("sorted list : " + mylist);


        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }

    /**
     * @param chatId The number of the chat
     * @return All the message history of this chat
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public ResponseEntity<List<ChatMessage>> AllMessageHistoryMainChat(@RequestParam String chatId) {
        System.out.println("----------chat messages---------");
        System.out.println(chatId instanceof String);
        System.out.println("chatId : " + chatId);
        List<ChatMessage> newList = chatService.getAllMessagesByChatId(chatId);
        logger.info(newList);
        System.out.println(newList);
        return ResponseEntity.status(HttpStatus.OK).body(newList); //list with all message of the "id" room.
    }

    public SubmitedUser addUserForTestToDB() throws NoSuchAlgorithmException {
        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
        if (userService.addForTest(user)) {
            return user;
        }
        return null;
    }

    public String addGuestForTestToDB() throws SQLDataException {
        SubmitedUser user = new SubmitedUser(" ", " ", "Saraa");
        Guest g = new Guest(user.getNickName());
        return userService.addGuest(g);
    }
}



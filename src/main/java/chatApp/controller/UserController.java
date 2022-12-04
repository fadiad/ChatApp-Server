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
     * @return list of all the guest users from the repo.
     */
    @RequestMapping(value = "onlineGuestUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Guest>> getGuestList() {
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

    /**
     * @param id of the user
     * @return search the user by id in the repo, and return the user in the response body
     */
    @RequestMapping(value = "userById", method = RequestMethod.POST) //TODO: problem
    public ResponseEntity<User> getUserById(@RequestBody String id) {
        System.out.println("------------User By ID-------------");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    /**
     * @return list of 'online' user from the user repo.
     */
    @RequestMapping(value = "onlineUsers", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserList() {
        System.out.println("------------online Users-------------");

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

//    public SubmitedUser addUserForTestToDB() throws NoSuchAlgorithmException {
//        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
//        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
//        if (userService.addForTest(user)) {
//            return user;
//        }
//        return null;
//    }

    public String addGuestForTestToDB() throws SQLDataException {
        SubmitedUser user = new SubmitedUser(" ", " ", "Saraa");
        Guest g = new Guest(user.getNickName());
        return userService.addGuest(g);
    }
}



package chatApp.controller;

import chatApp.Entities.*;
import chatApp.service.UserService;
import chatApp.util.EmailActivation;
import chatApp.util.ValidationUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody SubmitedUser user) throws SQLDataException, NoSuchAlgorithmException {
        System.out.println("------------Registered User login-------------");
        System.out.println(user);
        String token = "";

        if (ValidationUtils.loginUserValidation(user)) {
            token = userService.login(user);
        }

        if (token == null)
            throw new SQLDataException(String.format("email or password is not correct !"));

        return ResponseEntity.ok(token);
    }


    @RequestMapping(value = "loginGuest", method = RequestMethod.POST)
    public ResponseEntity<String> loginGuest(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------guest login-------------");
        if (ValidationUtils.guestValidation(user)) {
            Guest guest = new Guest(user.getNickName());
            //It is need to be a guest need to send only name
            return ResponseEntity.status(HttpStatus.OK).body(userService.addGuest(guest));
        }
        throw new SQLDataException(String.format("Nickname \" %s \" is not valid!", user.getNickName()));
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody SubmitedUser user) throws SQLDataException {
        Response response = userService.addUser(user); //It is a user need to send full user
        Gson g = new Gson();
        return ResponseEntity
                .status(response.getStatus())
                .body(g.toJson(response.getMessage()));
    }


    @RequestMapping(value = "saveProfile", method = RequestMethod.POST)
    public ResponseEntity<String> saveProfile(@RequestBody User user) throws SQLDataException {

        System.out.println("in controller: " + user);
        Response response = userService.saveProfile(user);
        Gson g = new Gson();
        return ResponseEntity
                .status(response.getStatus())
                .body(g.toJson(response.getMessage()));
    }


    @RequestMapping(value = "logoutGuest", method = RequestMethod.POST)
    public ResponseEntity<String> logoutGuest(@RequestBody SubmitedUser user) {
        if (!userService.logoutGuest(user.getNickName()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("some error !");

        return ResponseEntity.ok("logout done successfully");
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@RequestBody String token) {

        if (!userService.logout(token))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("some error !");

        return ResponseEntity.ok("logout done successfully");
    }

    @RequestMapping(value = "activate", method = RequestMethod.GET)
    public ResponseEntity<Object> activateEmail(@RequestParam String code) throws NoSuchAlgorithmException {
        System.out.println("------------Activate account after login-------------");
        System.out.println(code);
        Response response = userService.enterUserToDB(code);

        return ResponseEntity
                .status(response.getStatus())
                .body(response.getMessage());
    }


    @RequestMapping(value = "onlineGuestUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Guest>> getGuestList() throws SQLDataException {
        System.out.println("------------online Users-------------");
        List<Guest> mylist = userService.getGuestList();
        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }

    @RequestMapping(value = "userByToken", method = RequestMethod.GET)
    public ResponseEntity<User> getUserByToken1(@RequestParam String token) throws SQLDataException {
        System.out.println("------------User By Token-------------");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token));
    }

//    @RequestMapping(value = "userByToken", method = RequestMethod.POST)
//    public ResponseEntity<User> getUserByToken(@RequestParam String token) throws SQLDataException {
//        System.out.println("------------User By Token-------------");
//        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token));
//    }


    @RequestMapping(value = "userById", method = RequestMethod.POST)
    public ResponseEntity<User> getUserById(@RequestBody String id) throws SQLDataException {
        System.out.println("------------User By ID-------------");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @RequestMapping(value = "onlineUsers", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserList() throws SQLDataException {
        System.out.println("------------online Users-------------");
//        List<User> mylist = userService.getUserList();

        List<User> mylist = userService.getUserList().stream().filter(user -> user.getStatus() != "offline")
                .sorted(Comparator.comparing(User::getRole))
                .collect(Collectors.toList());

        System.out.println("sorted list : " + mylist);


        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }
}



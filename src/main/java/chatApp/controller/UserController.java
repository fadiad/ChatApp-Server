package chatApp.controller;

import chatApp.Entities.*;
import chatApp.service.UserService;
import chatApp.util.EmailActivation;
import chatApp.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email or password is not correct !");

        return ResponseEntity.ok(token);
    }


    @RequestMapping(value = "loginGuest", method = RequestMethod.POST)
    public ResponseEntity<Guest> loginGuest(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------guest login-------------");
        if (ValidationUtils.guestValidation(user)) {
            Guest guest = new Guest(user.getNickName());
             //It is need to be a guest need to send only name
            return ResponseEntity.status(HttpStatus.OK).body(userService.addUser(guest));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody SubmitedUser user) throws SQLDataException {
        Response response = userService.addUser(user); //It is a user need to send full user
        return ResponseEntity
                .status(response.getStatus())
                .body(response.getMessage());
    }


    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@RequestHeader("token") String token) {

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
}



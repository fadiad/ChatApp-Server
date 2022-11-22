package chatApp.controller;

import chatApp.Entities.Guest;
import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------Registered User login-------------");

        String token = "";
        if (user != null) {
            token = userService.login(user);
            System.out.println(token);
        }

        if (token == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email or password is not correct !");

        return ResponseEntity.ok(token);
    }


    @RequestMapping(value = "loginGuest", method = RequestMethod.POST)
    public ResponseEntity<Guest> loginGuest(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------guest login-------------");
        if (user.getNickName() != "") {
            Guest guest = new Guest(user.getNickName());
             //It is need to be a guest need to send only name
            return ResponseEntity.status(HttpStatus.OK).body(userService.addUser(guest));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ResponseEntity<List<Guest>> createUser(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------signup-------------");
        System.out.println(user);
        if (user != null) {
            User myUser = new User.Builder(user.getEmail(), user.getPassword(), user.getNickName()).build();
            System.out.println("My user to add : " + myUser);
            userService.addUser(myUser).toString(); //It is a user need to send full user
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
    }

    @RequestMapping(value = "onlineGuestUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Guest>> getGuestList() throws SQLDataException {
        System.out.println("------------online Users-------------");
        List<Guest> mylist = userService.getGuestList();
        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }
}



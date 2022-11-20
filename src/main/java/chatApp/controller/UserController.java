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


    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.[0-9])(?=.[a-z]).{8,20}$");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]\\w{5,29}$");


    public boolean validatePassword(String password) {
//        Matcher m = PASSWORD_PATTERN.matcher(password);
//        boolean matchFound = m.matches();
//        if (matchFound) {
//            return true;
//        }
//        return false;
        if (password.length() >= 0)
            return true;
        return false;
    }

    public boolean validateName(String name) {
//        Matcher m = NAME_PATTERN.matcher(name);
//        boolean matchFound = m.matches();
//        if (matchFound) {
//            return true;
//        }
//        return false;
        if (name.length() > 0)
            return true;

        return false;
    }

    public boolean validateEmail(String email) {
//        Matcher m = emailPattern.matcher(email);
//        boolean matchFound = m.matches();
//        if (matchFound) {
//            return true;
//        }
//        return false;

        if (email.length() > 0) {
            return true;
        }

        return false;
    }


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------login-------------");
        System.out.println(user);
        if (user.getNickName() != "") {
            if (validateName(user.getNickName())) {
                Guest guest = new Guest(user.getNickName());
                userService.addUser(guest); //It is need to be a guest need to send only name
                System.out.println(userService.getAllGuests());
                return ResponseEntity.status(HttpStatus.OK).body(userService.getAllGuests());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is not ok");
            }
        } else if (user.getEmail() != "" && user.getPassword() != "" && user.getNickName() == "") {
            String token = userService.login(user);
            System.out.println(token);
            if (token == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email or password is not correct !");

            return ResponseEntity.ok(token);
        }

        return ResponseEntity.ok(null);
    }

//    @RequestMapping(value = "loginGuest", method = RequestMethod.POST)
//    public ResponseEntity<List<Guest>> loginGuest(@RequestBody SubmitedUser user) throws SQLDataException {
//        System.out.println("------------guest login-------------");
//        if (user.getNickName() != "") {
//            Guest guest = new Guest(user.getNickName());
//            userService.addUser(guest); //It is need to be a guest need to send only name
//            return ResponseEntity.status(HttpStatus.OK).body(userService.getAllGuests());
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
//    }


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

//    @RequestMapping(value = "signup", method = RequestMethod.POST)
//    public ResponseEntity<List<Guest>> createUser(@RequestBody SubmitedUser user) {
//
//        System.out.println("------------signup-------------");
//        System.out.println("This is the user : " + user);
//        try {
//            System.out.println(user + "++++++++++++++");
//            if (user.getEmail() == "" && user.getPassword() == "" && user.getNickName() != "") {
//                System.out.println("------------am in the first if-------------");
//                if (validateName(user.getNickName())) {
//                    Guest guest = new Guest(user.getNickName());
//                    userService.addUser(guest); //It is need to be a guest need to send only name
//                    System.out.println(userService.getAllGuests());
//                    return ResponseEntity.status(HttpStatus.OK).body(userService.getAllGuests());
//                } else {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is not ok");
//                }
//            } else if (user != null) {
//                System.out.println("------------am adding user-------------");
//                User myUser = new User.Builder(user.getEmail(), user.getPassword(), user.getNickName()).build();
//                System.out.println("My user to add : " + myUser);
//                userService.addUser(myUser).toString(); //It is a user need to send full user
//                return ResponseEntity.status(HttpStatus.OK).body(userService.getAllGuests());
//            } else {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                        "problem, You need to fill in all the details currently");
//            }
//        } catch (SQLDataException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists", e);
//        }
//    }
}
package chatApp.controller;

import chatApp.Entities.Guest;
import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
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

    @RequestMapping(method = RequestMethod.POST)
    public String createUser(@RequestBody SubmitedUser user) {
//        System.out.println("------------am on the top-------------");
//        System.out.println("This is the user : " + user);
        try {
//            System.out.println(user + "++++++++++++++");
//            if (user.getEmail() == "" && user.getPassword() == "" && user.getNickName() != "") {
//                System.out.println("------------am in the first if-------------");
//                if (validateName(user.getNickName())) {
//                    Guest guest = new Guest(user.getNickName());
//                    userService.addUser(guest); //It is need to be a guest need to send only name
//                    return userService.getAllGuests().toString();
//                } else {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is not ok");
//                }
//            } else
            if (true) {
                System.out.println("------------am adding user-------------");
                User myUser = new User.Builder(user.getEmail(), user.getPassword(), user.getNickName()).build();
                System.out.println("My user to add : " + myUser);
                return userService.addUser(myUser).toString(); //It is a user need to send full user
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "problem, You need to fill in all the details currently");
            }
        } catch (SQLDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }
}
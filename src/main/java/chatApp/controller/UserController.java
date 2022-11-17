package chatApp.controller;

import chatApp.Entities.Guest;
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
    private static UserService userService;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]\\w{5,29}$");

    private UserController() {

    }


    public boolean validatePassword(String password) {
//        Matcher m = PASSWORD_PATTERN.matcher(password);
//        boolean matchFound = m.matches();
//        if (matchFound) {
//            return true;
//        }
//        return false;
        if (password.length() >= 6)
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
        if (name.length() > 3)
            return true;

        return false;
    }

    public boolean validateEmail(String email) {
        Matcher m = emailPattern.matcher(email);
        boolean matchFound = m.matches();
        if (matchFound) {
            return true;
        }
        return false;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createUser(@RequestBody User user) {
        try {
            System.out.println(user+"++++++++++++++");
            if (user.getEmail() == "" && user.getPassword() == "" && user.getNickName() != "") {
                if (validateName(user.getNickName())) {
                    Guest guest = new Guest(user.getNickName());
                    return userService.addUser(guest).toString(); //It is need to be a guest need to send only name
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is not ok");
                }
            } else if (validateName(user.getNickName())
                    && validatePassword(user.getPassword())
                    && validateEmail(user.getEmail())) {
                return userService.addUser(user).toString(); //It is a user need to send full user
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "problem, You need to fill in all the details currently");
            }
        } catch (SQLDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }
}
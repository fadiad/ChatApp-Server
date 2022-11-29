package chatApp.controller;


import chatApp.Entities.Guest;
import chatApp.Entities.Response;
import chatApp.Entities.SubmitedUser;
import chatApp.service.UserService;
import chatApp.util.ValidationUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationController {

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
    public ResponseEntity<Object> loginGuest(@RequestBody SubmitedUser user) throws SQLDataException {
        System.out.println("------------guest login-------------");
        if (ValidationUtils.guestValidation(user)) {
            Guest guest = new Guest(user.getNickName());
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

    @RequestMapping(value = "logoutGuest", method = RequestMethod.POST)
    public ResponseEntity<String> logoutGuest(@RequestBody String token) {
        if (!userService.logoutGuest(token))
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

}

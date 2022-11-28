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

    @RequestMapping(value = "onlineGuestUsers", method = RequestMethod.GET)
    public ResponseEntity<List<Guest>> getGuestList() throws SQLDataException {
        System.out.println("------------online Users-------------");
        List<Guest> mylist = userService.getGuestList();
        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }

    @RequestMapping(value = "userByToken", method = RequestMethod.GET)
    public ResponseEntity<User> getUserByToken(@RequestParam String token) throws SQLDataException {
        System.out.println("------------User By Token-------------");
        System.out.println(token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByToken(token));
    }

    @RequestMapping(value = "onlineUsers", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserList() throws SQLDataException {
        System.out.println("------------online Users-------------");
//        List<User> mylist = userService.getUserList();

        List<User> mylist = userService.getUserList().stream()
                .sorted(Comparator.comparing(User::getRole))
                .collect(Collectors.toList());

        System.out.println("sorted list : " + mylist);


        System.out.println(mylist);
        return ResponseEntity.status(HttpStatus.OK).body(mylist);
    }
}



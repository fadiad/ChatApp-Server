package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.Guest;
import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;
import chatApp.util.ValidationUtils;

import com.google.gson.Gson;
import com.sun.tools.jconsole.JConsoleContext;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLDataException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ControllerTest {
    @Autowired
    UserController userController;
    @Autowired
    ChatController chatController;
    @Autowired
    AuthenticationController authenticationController;

    @AfterEach
    void setup() {
    }


    @Test
    public void getUserList_Successfully() {
        ResponseEntity<List<User>> r = userController.getUserList();
        System.out.println(r);

        assertEquals(200, r.getStatusCodeValue());
    }


//    @Test
//    public void login_User_Successfully() throws SQLDataException, NoSuchAlgorithmException {
//        userController.addUserForTestToDB(); //add user to DB for test only// saray
//        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
//        ResponseEntity<Object> r = userController.login(user);
//
//        assertEquals(200, r.getStatusCodeValue());
//    }

//    @Test
//    public void login_User_Not_Successfully_Bad_Details() {
//        SubmitedUser user = new SubmitedUser("sarayshlomi1@gmail.com", "123456", "");
//
//        assertThrows(SQLDataException.class, () -> userController.login(user), String.format("email or password is not correct !"));
//    }

//    @Test
//    @Disabled
//    public void login_Guest_Successfully() throws SQLDataException {
//        SubmitedUser user = new SubmitedUser(" ", " ", "Sarah");
////        System.out.println(user);
//        userController.loginGuest(user);
//
//        assertTrue(200 == 200);
//    }


//    @Test
//    public void create_User_Successfully() throws SQLDataException {
//        SubmitedUser user = new SubmitedUser("gffrvtcvrfvb@gmail.com", "123456", "Saray");
//        ResponseEntity<String> r = userController.createUser(user);
//
//        assertEquals(200, r.getStatusCodeValue());
//    }
//
//    @Test
//    public void create_User_Not_Successfully() {
//        SubmitedUser user = new SubmitedUser("g", "1", "S");
//
//        assertThrows(SQLDataException.class, () -> userController.createUser(user), "The details not valid");
//    }

    @Test
    public void saveProfile_Successfully() throws SQLDataException, NoSuchAlgorithmException {
        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();
        ResponseEntity<String> r = userController.saveProfile(myUser);

        assertEquals(200, r.getStatusCodeValue(), "new profile saved successfully!");
    }

    @Test
    public void saveProfile_Not_Successfully_BadNickName() throws NoSuchAlgorithmException {
        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "S");
        User myUser = new User.Builder(user.getEmail(), ValidationUtils.secretPassword(user.getPassword()), user.getNickName()).build();

        assertThrows(SQLDataException.class, () -> userController.saveProfile(myUser), String.format("Nickname \" %s \" is not valid!", user.getNickName()));
    }

//    @Test
//    public void activateEmail_Bad_Response_UserNotExist() throws NoSuchAlgorithmException {
//        String str = "123";
//        ResponseEntity<Object> res = userController.activateEmail(str);
//
//        assertEquals(res.getStatusCodeValue(), 400, "user tou want to activate not exist");
//    }

    @Test
    public void getGuestList_Successfully() {
        ResponseEntity<List<Guest>> r = userController.getGuestList();

        assertEquals(200, r.getStatusCodeValue());
    }

//    @Test
//    public void AllMessageHistoryMainChat_Successfully() {
//        ChatController.RecievedMessage recievedMessage = new ChatController.RecievedMessage("sara", "hi");
//        chatController.sendPlainMessage(recievedMessage);
//        ResponseEntity<List<ChatMessage>> newList = userController.AllMessageHistoryMainChat("0");
//
//        assertNotNull(newList.getBody());
//        assertEquals(200, newList.getStatusCodeValue());
//    }

//    @Test
//    public void login_Guest_Not_Successfully_Bad_Details() {
//        SubmitedUser user = new SubmitedUser(" ", " ", "T");
//
//        assertThrows(SQLDataException.class, () -> userController.loginGuest(user), String.format("Nickname \" %s \" is not valid!", user.getNickName()));
//    }
//
//    @Test
//    public void User_Logs_In_Successfully_Response200() throws SQLDataException, NoSuchAlgorithmException {
//        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
//        ResponseEntity<Object> r = userController.login(user);
//        System.out.println(r.getStatusCode());
//
//        assertEquals(200, r.getStatusCodeValue());
//    }

//    @Test
//    public void getUserByToken_Successfully() throws SQLDataException, NoSuchAlgorithmException {
////        SubmitedUser user = userController.addUserForTestToDB();
//        SubmitedUser user = new SubmitedUser("saraysara1996@gmail.com", "12345", "Saray");
//        ResponseEntity<Object> a = userController.login(user);
//
//        assertTrue(userController.getUserByToken1((String) a.getBody()).getBody() instanceof User);
//    }


//    @Test
//    public void try_Login_Exist_Guest_Not_Successfully() throws SQLDataException {
//        SubmitedUser user = new SubmitedUser(" ", " ", "Saraayus");
//        userController.loginGuest(user);
//
//        assertThrows(SQLDataException.class, () -> userController.loginGuest(user));
//    }

    @Test
    public void logoutGuest_Successfully() throws SQLDataException {
        SubmitedUser user = new SubmitedUser(" ", " ", "moose7");
        System.out.println(user);
        Object x = authenticationController.loginGuest(user).getBody();
        System.out.println(x);
        String myx = "{\"token\" : \"" + x.toString() + "\"}";
        ResponseEntity<String> r = authenticationController.logoutGuest(myx);

        assertEquals(200, r.getStatusCode().value());
    }

//    @Test
//    @Disabled
//    public void logoutGuest_Not_Successfully_toUserThatNotLogIn() {
//        SubmitedUser user = new SubmitedUser(" ", " ", "qqq");
//        ResponseEntity<String> r1 = userController.logoutGuest(user);
//        ResponseEntity<String> r2 = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some error !");
//
//        assertTrue(r1 == r2);
//    }

//    @Test
//    @Disabled
//    public void logout_Successfully() throws NoSuchAlgorithmException {
//        SubmitedUser user = userController.addUserForTestToDB();
//
//        assertTrue(userController.logout("1").getBody().equals("logout done successfully"));
//    }

//    @Test
//    @Disabled
//    public void logout_Not_Successfully_toUserThatNotLogIn() {
//        ResponseEntity<String> r1 = userController.logout("1");
//        ResponseEntity<String> r2 = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some error !");
//
//        assertTrue(r1 == r2);
//    }

//    @Test
//    @Disabled
//    public void logoutUser_Not_Successfully()  {
//        SubmitedUser user = new SubmitedUser();
//        ResponseEntity<String> r = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some error !");
//        assertEquals(r, userController.logout("1111"));
//    }

//    @Test
//    @Disabled
//    public void User_Logs_In_Not_Successfully() {
//        SubmitedUser user = new SubmitedUser("saraysa@gmail.com", "1245677", "Fadi");
//        assertThrows(SQLDataException.class, () -> userController.login(user), String.format("email or password is not correct !"));
//    }

//    @Test
//    @Disabled
//    public void login_Guest_not_Successfully_NotValidName() {
//        SubmitedUser user = new SubmitedUser(" ", " ", "a");
//        assertThrows(SQLDataException.class, () -> userController.loginGuest(user));
//    }

//
//    @Test
//    @Disabled
//    public void logoutGuestt_Successfully() throws SQLDataException {
//        SubmitedUser user = new SubmitedUser(" ", " ", "Sarayyy");
//        userController.loginGuest(user);
//        ResponseEntity<String> r = userController.logoutGuest(user);
//        assertEquals(200, r.getStatusCodeValue());
//    }
}
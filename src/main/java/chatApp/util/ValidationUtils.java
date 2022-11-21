package chatApp.util;

import chatApp.Entities.SubmitedUser;

import java.util.regex.Pattern;

public class ValidationUtils {


    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.[0-9])(?=.[a-z]).{8,20}$");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]\\w{5,29}$");


    public static boolean validateName(String name) {
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


    public static boolean validateEmail(String email) {
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


    public static boolean validatePassword(String password) {
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


    public static boolean registrationUserValidation(SubmitedUser user) {
        if (validateEmail(user.getEmail()) && validatePassword(user.getPassword()) && validateName(user.getNickName())) {
            return true;
        }
        return false;
    }

    public static boolean loginUserValidation(SubmitedUser user) {
        if (validateEmail(user.getEmail()) && validatePassword(user.getPassword())) {
            return true;
        }
        return false;
    }

    public static boolean guestValidation(SubmitedUser user) {
        if (user != null && validateName(user.getNickName())) {
            return true;
        }
        return false;
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

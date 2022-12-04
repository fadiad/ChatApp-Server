package chatApp.util;

import chatApp.Entities.SubmitedUser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
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
        if (name.length() > 3)
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
        if (password.length() >= 4)
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



    public static String generateRandomToken() {
        int token = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        return String.valueOf(token);
    }
    public static String secretPassword(String password) throws IllegalArgumentException, NoSuchAlgorithmException {
        return toHexString(getSHA(password));
    }
    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        /* MessageDigest instance for hashing using SHA256 */
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        /* digest() method called to calculate message digest of an input and return array of byte */
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    private static String toHexString(byte[] hash) {
        /* Convert byte array of hash into digest */
        BigInteger number = new BigInteger(1, hash);
        /* Convert the digest into hex value */
        StringBuilder hexString = new StringBuilder(number.toString(16));
        /* Pad with leading zeros */
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}

package chatApp.util;

import chatApp.Entities.SubmitedUser;
import chatApp.Entities.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailActivation {

    private static String to;
    private static final String from = "saraysara1996@gmail.com";
    private static String host = "smtp.sendgrid.net";
    private static Properties properties = System.getProperties();

    static Session session = Session.getInstance(properties, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("apikey", "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");
        }
    });

    private static MimeMessage prepare() {
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.fallback", "true");
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        return message;
    }

    public static void sendSuccessRegisterationMessageToUser(SubmitedUser user) {
        String.valueOf(user.getEmail());
        to = user.getEmail();
        MimeMessage message = prepare();
        String registerationSuccessMessage = "You are registered successfully ! you can go to login page by the link : " + "http://localhost:9000/";
        try {
            message = prepareThisMessage(message, user, registerationSuccessMessage);
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    public static void sendEmailWithGenerateCode(String code, SubmitedUser user) {
        String.valueOf(user.getEmail());
        to = user.getEmail();
        MimeMessage message = prepare();
        String link = "Please activate Your ChatApp Account Now, click on the link:" + "http://localhost:8080/auth/activate?code=" + code;

        try {
            message = prepareThisMessage(message, user, link);
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    private static MimeMessage prepareThisMessage(MimeMessage message, SubmitedUser user, String messageText) throws MessagingException {
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(user.getNickName() + "," + "welcome to the ChatApp application!");
        message.setText(messageText);
        System.out.println("sending...");
        return message;
    }

}

package chatApp.util;

import chatApp.Entities.SubmitedUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static chatApp.util.EmailActivation.sendEmailWithGenerateCode;
import static chatApp.util.EmailActivation.sendSuccessRegisterationMessageToUser;

class EmailActivationTest {

    @Test
    void sendSuccessRegisterationMessageToUserTest() {
        SubmitedUser user1 = new SubmitedUser("saraysara1996@gmail.com", "sdzfg", "Sarayy");
        sendSuccessRegisterationMessageToUser(user1);
    }

    @Test
    void sendEmailWithGenerateCodeT() {
        SubmitedUser user1 = new SubmitedUser("saraysara1996@gmail.com", "sdzfg", "Sarayy");
        sendEmailWithGenerateCode("123", user1);
    }
}
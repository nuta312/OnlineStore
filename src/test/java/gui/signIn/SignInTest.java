package gui.signIn;

import gui.BaseGUI;
import kg.benext.common.model.User;
import kg.benext.common.utils.data.RandomData;
import kg.benext.gui.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignInTest  extends BaseGUI {
    @Test
    @DisplayName("Login with valid credentials")
    void loginUserTest() {
        User user = RandomData.defaultUser();

        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .inputEmail(user.getEmail())
                .inputPassword(user.getPassword())
                .clickToSignIn()
                .waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }

    @Test
    @DisplayName("Login with not registered credentials")
    void loginWithNotValidUserTest() {
        User user = RandomData.randomUser();

        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .inputEmail(user.getEmail())
                .inputPassword(user.getPassword())
                .clickToSignInExpectError()
                .waitForPageToBeLoaded()
                .verifyAuthError();
    }
}

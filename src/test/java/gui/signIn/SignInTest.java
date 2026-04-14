package gui.signIn;

import gui.BaseGUI;
import kg.benext.gui.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignInTest extends BaseGUI {

    @Test
    @DisplayName("Sign in with valid credentials")
    void signInTest() {
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .clickToSignIn().waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }
}

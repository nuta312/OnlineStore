package gui.signUp;

import com.codeborne.selenide.Selenide;
import gui.BaseGUI;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.gui.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignUpTest extends BaseGUI {

    @Test
    @DisplayName("Create user with valid Credentials")
    void createUserTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
            new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickToCreateAccountBtn().waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }
    // test is displayed Email is already in use with red font color
    @Test
    @DisplayName("Create user with same Credentials")
    void createUserTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickToCreateAccountBtn().waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }
}
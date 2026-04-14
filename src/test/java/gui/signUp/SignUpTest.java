package gui.signUp;

import com.codeborne.selenide.Selenide;
import gui.BaseGUI;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.gui.pages.HomePage;
import kg.benext.gui.pages.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class SignUpTest extends BaseGUI {

    @Test
    @DisplayName("Create user with valid Credentials")
    void createUserTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
            new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("Jane Doe")
                .inputEmail("JaneDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountSuccess().waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }
    // test is displayed Email is already in use with red font color
    @Test
    @DisplayName("Create user with same Credentials")
    void createUserWithSameCredentialTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyEmailInUseMessage();
    }

    @Test
    @DisplayName("Create user with empty full name")
    void createUserWithEmptyFullNameTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyFullNameIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with one letter full name")
    void createUserWithNotValidFullNameTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("J")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyFullNameIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with empty email")
    void createUserWithEmptyEmailTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyEmailIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with not valid email")
    void createUserWithNotValidEmailTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyEmailIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with empty password")
    void createUserWithEmptyPasswordTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputConfirmPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyPasswordIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with less characters password")
    void createUserWithLessCharactersPasswordTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("12345")
                .inputConfirmPassword("12345")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyPasswordIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with empty confirm password")
    void createUserWithEmptyConfirmPasswordTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .acceptTerms()
                .clickCreateAccountExpectError()
                .verifyConfirmPasswordIsRequiredMessage();
    }

    @Test
    @DisplayName("Create user with empty confirm password")
    void createUserWithNoTermsAcceptTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .inputFullName("John Doe")
                .inputEmail("JohnDoe@exapmle.com")
                .inputPassword("123456")
                .inputConfirmPassword("123456")
                .clickCreateAccountExpectError()
                .verifyTermsIsRequiredMessage();
    }

    //Написать Тест на проверку ввода имени только из ЦИФР

    @Test
    @DisplayName("Register by Google Auth and Check if new window is accounts.google.com")
    void createUserByGoogle() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .clickToRegisterByGoogle();
                switchTo().window(1);
                webdriver().shouldHave(urlContaining("accounts.google.com"));
    }

    @Test
    @DisplayName("Verify all placeholders")
    void verifyPlaceholdersTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .verifyFullNamePlaceholder()
                .verifyEmailPlaceholder()
                .verifyPasswordPlaceholder()
                .verifyConfirmPasswordPlaceholder();
    }
}
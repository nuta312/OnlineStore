package gui.signUp;

import gui.BaseGUI;
import kg.benext.common.model.User;
import kg.benext.common.utils.data.RandomData;
import kg.benext.gui.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class SignUpTest extends BaseGUI {

    @Test
    @DisplayName("Create user with valid Credentials")
    void createUserTest() {
        User user = RandomData.randomUser();
            new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .fillRegistrationForm(user)
                .acceptTerms()
                .clickCreateAccountSuccess().waitForPageToBeLoaded()
                .verifyProfileImageIsDisplayed();
    }
    // test is displayed Email is already in use with red font color
    @Test
    @DisplayName("Create user with same Credentials")
    void createUserWithSameCredentialTest() {
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
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .clickToRegisterBtn().waitForPageToBeLoaded()
                .verifyFullNamePlaceholder()
                .verifyEmailPlaceholder()
                .verifyPasswordPlaceholder()
                .verifyConfirmPasswordPlaceholder();
    }
}
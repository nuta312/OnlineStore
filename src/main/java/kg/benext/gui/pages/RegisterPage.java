package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage extends BasePage<RegisterPage>{
    private final String PLACEHOLDER = "placeholder";
    private final SelenideElement fullName = $("input[name='fullName']");
    private final SelenideElement email = $("input[name='email']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement confirmPassword = $("input[name='confirmPassword']");
    private final SelenideElement terms = $("#terms");
    private final SelenideElement createAccount = $x("//button[text()='Create Account']");
    private final SelenideElement emailInUseNotification = $x("//div[text()='Email is already in use']");
    private final SelenideElement fullNameErrorMessage = $x("//p[text()='Name must be at least 2 characters']");
    private final SelenideElement emailErrorMessage = $x("//p[text()='Please enter a valid email address']");
    private final SelenideElement passwordErrorMessage = $x("//p[text()='Password must be at least 6 characters']");
    private final SelenideElement confirmPasswordErrorMessage = $x("//p[contains(text(), \"Passwords don't match\")]");
    private final SelenideElement termsErrorMessage = $x("//p[text()='You must accept the terms and conditions']");
    private final SelenideElement google = $x("//button[text()='Google']");

    @Override
    public RegisterPage waitForPageToBeLoaded() {
        $("h1").shouldHave(exactText("Create Account"));
        return this;
    }

    public RegisterPage inputFullName(String fullName) {
        this.fullName.shouldBe(visible).sendKeys(fullName);
        return this;
    }

    public RegisterPage inputEmail(String email) {
        this.email.shouldBe(visible).sendKeys(email);
        return this;
    }

    public RegisterPage inputPassword(String password) {
        this.password.shouldBe(visible).sendKeys(password);
        return this;
    }

    public RegisterPage inputConfirmPassword(String confirmPassword) {
        this.confirmPassword.shouldBe(visible).sendKeys(confirmPassword);
        return this;
    }

    public RegisterPage acceptTerms() {
        terms.shouldBe(visible).click();
        return this;
    }

    public HomePage clickCreateAccountSuccess() {
        createAccount.shouldBe(visible).click();
        return new HomePage();
    }

    public RegisterPage clickCreateAccountExpectError() {
        createAccount.shouldBe(visible).click();
        return this;
    }

    public RegisterPage verifyEmailInUseMessage() {
        emailInUseNotification.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage verifyFullNameIsRequiredMessage() {
        fullNameErrorMessage.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage verifyEmailIsRequiredMessage() {
        emailErrorMessage.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage verifyPasswordIsRequiredMessage() {
        passwordErrorMessage.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage verifyConfirmPasswordIsRequiredMessage() {
        confirmPasswordErrorMessage.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage verifyTermsIsRequiredMessage() {
        termsErrorMessage.shouldBe(visible).shouldHave(cssClass("text-red-500"));
        return this;
    }

    public RegisterPage clickToRegisterByGoogle() {
        google.shouldBe(visible).click();
        return this;
    }

    public RegisterPage verifyFullNamePlaceholder() {
        fullName.shouldHave(attribute(PLACEHOLDER, "John Doe"));;
        return this;
    }

    public RegisterPage verifyEmailPlaceholder() {
        email.shouldHave(attribute(PLACEHOLDER, "name@example.com"));;
        return this;
    }

    public RegisterPage verifyPasswordPlaceholder() {
        password.shouldHave(attribute(PLACEHOLDER, "Create a password"));;
        return this;
    }

    public RegisterPage verifyConfirmPasswordPlaceholder() {
        confirmPassword.shouldHave(attribute(PLACEHOLDER, "Confirm your password"));;
        return this;
    }
}
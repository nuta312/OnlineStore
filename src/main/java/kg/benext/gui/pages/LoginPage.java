package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends BasePage<LoginPage>{
    private final SelenideElement registerBtn = $("a[href='/register']");
    private final SelenideElement email = $("input[name='email']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement signInBtn = $x("//button[text()='Sign In']");
private final SelenideElement authError = $$("div").findBy(text("Invalid email or password"));

    @Override
    public LoginPage waitForPageToBeLoaded() {
        $x("//h1[text()='Welcome Back']").shouldBe(visible);
        return this;
    }

    public RegisterPage clickToRegisterBtn() {
        registerBtn.shouldBe(visible).click();
        return new RegisterPage();
    }

    public LoginPage inputEmail(String email) {
        this.email.shouldBe(visible).sendKeys(email);
        return this;
    }

    public LoginPage inputPassword(String password) {
        this.password.shouldBe(visible).sendKeys(password);
        return this;
    }

    public HomePage clickToSignIn() {
        signInBtn.shouldBe(visible).click();
        return new HomePage();
    }

    public LoginPage clickToSignInExpectError() {
        signInBtn.shouldBe(visible).click();
        return this;
    }

    public LoginPage verifyAuthError(){
        authError.shouldBe(visible);
        return this;
    }
}